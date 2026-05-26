package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.ReservaFormDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Alojamento;
import pt.hotel.animais.model.Reserva;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.model.enums.EstadoReserva;
import pt.hotel.animais.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Serviço para gerenciar reservas.
 * Implementa as regras de negócio para criar, modificar e cancelar reservas,
 * incluindo validação de disponibilidade e prevenção de overbooking.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService implements IReservaService {
    
    private final ReservaRepository reservaRepository;
    private final ITutorService tutorService;
    private final IAnimalService animalService;
    private final IAvailabilityDomainService availabilityDomainService;
    
    /**
     * Cria uma nova reserva com validações rigorosas.
     * 
     * Validações:
     * 1. Tutor, animal e alojamento existem
     * 2. Animal pertence ao tutor
     * 3. Data de fim > data de início
     * 4. Alojamento está disponível (limpo e sem conflitos)
     */
    public Reserva criar(ReservaFormDto formDto) {
        // Valida que tutor existe
        Tutor tutor = tutorService.obter(formDto.getTutorId());
        
        // Valida que animal existe
        Animal animal = animalService.obter(formDto.getAnimalId());
        
        // Valida que animal pertence ao tutor
        if (!animal.getTutor().getId().equals(tutor.getId())) {
            throw new IllegalArgumentException("O animal não pertence ao tutor especificado");
        }
        
        // Valida as datas
        if (formDto.getDataInicio() == null || formDto.getDataFim() == null) {
            throw new IllegalArgumentException("Datas de entrada obrigatórias");
        }
        
        if (!formDto.getDataInicio().isBefore(formDto.getDataFim())) {
            throw new IllegalArgumentException("Data de início deve ser anterior a data de fim");
        }
        
        // Verifica overbooking antes do lock para devolver erro explícito de reserva sobreposta.
        long conflitos = reservaRepository.countActiveInPeriod(
            formDto.getAlojamentoId(),
            formDto.getDataInicio(),
            formDto.getDataFim()
        );
        
        if (conflitos > 0) {
            throw new IllegalArgumentException(
                "O alojamento já tem reservas ativas ou confirmadas neste período. Conflito: overbooking não permitido."
            );
        }
        
        // Valida a regra completa de disponibilidade com lock pessimista no alojamento.
        Alojamento alojamento = availabilityDomainService.validarDisponivelParaReservaComLock(
            formDto.getAlojamentoId(),
            formDto.getDataInicio(),
            formDto.getDataFim(),
            animal.getEspecie()
        );
        
        // Cria a reserva
        Reserva reserva = new Reserva();
        reserva.setTutor(tutor);
        reserva.setAnimal(animal);
        reserva.setAlojamento(alojamento);
        reserva.setDataInicio(formDto.getDataInicio());
        reserva.setDataFim(formDto.getDataFim());
        reserva.setEstado(EstadoReserva.ATIVA);
        
        return reservaRepository.save(reserva);
    }
    
    /**
     * Procura uma reserva pelo ID.
     */
    @Transactional(readOnly = true)
    public Reserva obter(Long id) {
        return reservaRepository.findWithDetalhesById(id)
            .orElseThrow(() -> new IllegalArgumentException("Reserva com ID " + id + " não encontrada"));
    }
    
    /**
     * Procura todas as reservas de um tutor.
     */
    @Transactional(readOnly = true)
    public List<Reserva> procurarPorTutor(Long tutorId) {
        return reservaRepository.findByTutorId(tutorId);
    }
    
    /**
     * Procura todas as reservas de um animal.
     */
    @Transactional(readOnly = true)
    public List<Reserva> procurarPorAnimal(Long animalId) {
        return reservaRepository.findByAnimalId(animalId);
    }
    
    /**
     * Procura todas as reservas ativas de um tutor (ordenadas por data de criação, mais recentes primeiro).
     */
    @Transactional(readOnly = true)
    public List<Reserva> procurarAtivas(Long tutorId) {
        return reservaRepository.findByTutorIdOrderByDataCriacaoDesc(tutorId);
    }
    
    /**
     * Procura todas as reservas de um alojamento.
     */
    @Transactional(readOnly = true)
    public List<Reserva> procurarPorAlojamento(Long alojamentoId) {
        return reservaRepository.findByAlojamentoId(alojamentoId);
    }
    
    /**
     * Cancela uma reserva.
     * Uma reserva cancelada não pode ser reativada (regra de negócio).
     */
    public Reserva cancelar(Long id) {
        Reserva reserva = obter(id);
        
        if (!reserva.podeSerCancelada()) {
            throw new IllegalArgumentException(
                "Reserva não pode ser cancelada no estado: " + reserva.getEstado()
            );
        }
        
        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }
    
    /**
     * Confirma uma reserva ativa durante o check-in.
     */
    public Reserva confirmar(Long id) {
        Reserva reserva = obter(id);

        if (reserva.getEstado() != EstadoReserva.ATIVA) {
            throw new IllegalArgumentException(
                "Apenas reservas ativas podem ser confirmadas"
            );
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    /**
     * Marca uma reserva confirmada como concluída após check-out da estadia.
     */
    public Reserva concluir(Long id) {
        Reserva reserva = obter(id);

        if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
            throw new IllegalArgumentException(
                "Apenas reservas confirmadas podem ser concluídas após check-out"
            );
        }

        reserva.setEstado(EstadoReserva.CONCLUIDA);
        return reservaRepository.save(reserva);
    }

    /**
     * Conta as reservas ativas.
     */
    @Transactional(readOnly = true)
    public long contarReservasAtivas() {
        return reservaRepository.countByEstado(EstadoReserva.ATIVA);
    }

    /**
     * Conta reservas ativas com início futuro.
     */
    @Transactional(readOnly = true)
    public long contarReservasFuturas() {
        return reservaRepository.countFuturas(LocalDate.now());
    }
    
    /**
     * Lista todas as reservas.
     */
    @Transactional(readOnly = true)
    public List<Reserva> listarTodas() {
        return reservaRepository.findAllWithDetalhes();
    }
}
