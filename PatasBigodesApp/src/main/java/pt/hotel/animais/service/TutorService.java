package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.TutorFormDto;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.repository.TutorRepository;

import java.util.List;

/**
 * Serviço para gerenciar tutores.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TutorService implements ITutorService {
    
    private final TutorRepository tutorRepository;
    
    /**
     * Regista um novo tutor.
     * Valida a unicidade do NIF.
     */
    public Tutor registar(TutorFormDto formDto) {
        // Verifica se o NIF já existe
        if (tutorRepository.findByNif(formDto.getNif()).isPresent()) {
            throw new IllegalArgumentException("Um tutor com este NIF já existe");
        }
        
        // Cria e persiste o tutor
        Tutor tutor = new Tutor();
        tutor.setNome(formDto.getNome());
        tutor.setNif(formDto.getNif());
        tutor.setContacto(formDto.getContacto());
        tutor.setEmail(formDto.getEmail());
        
        return tutorRepository.save(tutor);
    }
    
    /**
     * Procura um tutor pelo NIF.
     */
    @Transactional(readOnly = true)
    public Tutor procurarPorNif(String nif) {
        return tutorRepository.findByNif(nif)
            .orElseThrow(() -> new IllegalArgumentException("Tutor com NIF " + nif + " não encontrado"));
    }
    
    /**
     * Procura tutores pelo nome (busca parcial).
     */
    @Transactional(readOnly = true)
    public List<Tutor> procurarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome deve ser fornecido para busca");
        }
        return tutorRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Procura um tutor pelo ID.
     */
    @Transactional(readOnly = true)
    public Tutor obter(Long id) {
        return tutorRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Tutor com ID " + id + " não encontrado"));
    }
    
    /**
     * Lista todos os tutores.
     */
    @Transactional(readOnly = true)
    public List<Tutor> listarTodos() {
        return tutorRepository.findAll();
    }
    
    /**
     * Atualiza os dados de um tutor.
     */
    public Tutor atualizar(Long id, TutorFormDto formDto) {
        Tutor tutor = obter(id);
        
        // Valida se o novo NIF não conflita (a menos que seja o mesmo)
        if (!tutor.getNif().equals(formDto.getNif())) {
            if (tutorRepository.findByNif(formDto.getNif()).isPresent()) {
                throw new IllegalArgumentException("Um tutor com este NIF já existe");
            }
        }
        
        tutor.setNome(formDto.getNome());
        tutor.setNif(formDto.getNif());
        tutor.setContacto(formDto.getContacto());
        tutor.setEmail(formDto.getEmail());
        
        return tutorRepository.save(tutor);
    }
    
    /**
     * Elimina um tutor.
     */
    public void eliminar(Long id) {
        Tutor tutor = obter(id);
        tutorRepository.delete(tutor);
    }
}
