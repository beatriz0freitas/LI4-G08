package pt.hotel.animais.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.dto.AnimalFormDto;
import pt.hotel.animais.model.Animal;
import pt.hotel.animais.model.Tutor;
import pt.hotel.animais.repository.AnimalRepository;

import java.util.List;

/**
 * Serviço para gerenciar animais.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AnimalService {
    
    private final AnimalRepository animalRepository;
    private final TutorService tutorService;
    
    /**
     * Regista um novo animal associado a um tutor.
     */
    public Animal registar(AnimalFormDto formDto) {
        // Valida que o tutor existe
        Tutor tutor = tutorService.obter(formDto.getTutorId());
        
        // Cria e persiste o animal
        Animal animal = new Animal();
        animal.setTutor(tutor);
        animal.setNome(formDto.getNome());
        animal.setEspecie(formDto.getEspecie());
        animal.setRaca(formDto.getRaca());
        animal.setDataNascimento(formDto.getDataNascimento());
        animal.setPeso(formDto.getPeso());
        animal.setEstadoSaude(formDto.getEstadoSaude());
        animal.setNecessidadesAlimentares(formDto.getNecessidadesAlimentares());
        animal.setMedicacaoCurso(formDto.getMedicacaoCurso());
        
        return animalRepository.save(animal);
    }
    
    /**
     * Procura um animal pelo ID.
     */
    @Transactional(readOnly = true)
    public Animal obter(Long id) {
        return animalRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Animal com ID " + id + " não encontrado"));
    }
    
    /**
     * Procura todos os animais de um tutor.
     */
    @Transactional(readOnly = true)
    public List<Animal> procurarPorTutor(Long tutorId) {
        return animalRepository.findByTutorId(tutorId);
    }
    
    /**
     * Procura animais pelo nome (busca parcial).
     */
    @Transactional(readOnly = true)
    public List<Animal> procurarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome deve ser fornecido para busca");
        }
        return animalRepository.findByNomeContainingIgnoreCase(nome);
    }
    
    /**
     * Lista todos os animais.
     */
    @Transactional(readOnly = true)
    public List<Animal> listarTodos() {
        return animalRepository.findAll();
    }
    
    /**
     * Atualiza os dados de um animal.
     */
    public Animal atualizar(Long id, AnimalFormDto formDto) {
        Animal animal = obter(id);
        
        animal.setNome(formDto.getNome());
        animal.setEspecie(formDto.getEspecie());
        animal.setRaca(formDto.getRaca());
        animal.setDataNascimento(formDto.getDataNascimento());
        animal.setPeso(formDto.getPeso());
        animal.setEstadoSaude(formDto.getEstadoSaude());
        animal.setNecessidadesAlimentares(formDto.getNecessidadesAlimentares());
        animal.setMedicacaoCurso(formDto.getMedicacaoCurso());
        
        return animalRepository.save(animal);
    }
    
    /**
     * Elimina um animal.
     */
    public void eliminar(Long id) {
        Animal animal = obter(id);
        animalRepository.delete(animal);
    }
}
