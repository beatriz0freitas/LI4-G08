package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.EventoClinicoDto;
import pt.hotel.animais.dto.FichaClinicaDto;
import pt.hotel.animais.model.AlteracaoEstadoSaude;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Estadia;
import pt.hotel.animais.model.IntervencaoClinica;
import pt.hotel.animais.repository.AlteracaoEstadoSaudeRepository;
import pt.hotel.animais.repository.AnimalRepository;
import pt.hotel.animais.repository.EstadiaRepository;
import pt.hotel.animais.repository.IntervencaoClinicaRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClinicaService implements IClinicaService {

    private final AnimalRepository animalRepository;
    private final EstadiaRepository estadiaRepository;
    private final IntervencaoClinicaRepository intervencaoClinicaRepository;
    private final AlteracaoEstadoSaudeRepository alteracaoEstadoSaudeRepository;

    @Override
    public List<Animal> listarAnimais() {
        return animalRepository.findAll();
    }

    @Override
    public FichaClinicaDto obterFichaAnimal(Long animalId) {
        Animal animal = animalRepository.findById(animalId)
            .orElseThrow(() -> new IllegalArgumentException("Animal não encontrado"));
        Estadia estadia = estadiaRepository.findByAnimalIdComDetalhes(animalId).stream().findFirst().orElse(null);
        return new FichaClinicaDto(animal, estadia, construirTimeline(animalId));
    }

    @Override
    public Optional<Long> obterAnimalIdPorEstadia(Long estadiaId) {
        return estadiaRepository.findByIdComDetalhes(estadiaId)
            .map(Estadia::getReserva)
            .map(reserva -> reserva.getAnimal().getId());
    }

    private List<EventoClinicoDto> construirTimeline(Long animalId) {
        List<EventoClinicoDto> eventos = new ArrayList<>();
        for (AlteracaoEstadoSaude alteracao : alteracaoEstadoSaudeRepository.findByEstadiaReservaAnimalIdOrderByDataHoraDesc(animalId)) {
            String severidade = alteracao.getSeveridade() != null ? alteracao.getSeveridade().getLabel() : "Por definir";
            eventos.add(new EventoClinicoDto(
                "ALTERAÇÃO DE SAÚDE",
                "badge-danger",
                "st-ocupado",
                alteracao.getDataHora(),
                alteracao.getDescricao(),
                severidade
            ));
        }
        for (IntervencaoClinica intervencao : intervencaoClinicaRepository.findByEstadiaReservaAnimalIdOrderByDataHoraDesc(animalId)) {
            BigDecimal custo = intervencao.getCusto() != null ? intervencao.getCusto() : BigDecimal.ZERO;
            eventos.add(new EventoClinicoDto(
                "INTERVENÇÃO VETERINÁRIA",
                "badge-info",
                "st-reservado",
                intervencao.getDataHora(),
                intervencao.getDescricao(),
                "Custo: " + custo
            ));
        }
        eventos.sort(Comparator.comparing(EventoClinicoDto::getDataHora, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return eventos;
    }
}
