package pt.hotel.animais.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.repository.ColaboradorRepository;

/**
 * Adaptador entre colaboradores persistidos e o mecanismo de autenticação Spring Security.
 *
 * Carrega apenas colaboradores existentes na base de dados e usa o
 * {@code tipoColaborador} como role de autorização.
 */
@Service
public class ColaboradorUserDetailsService implements UserDetailsService {

    private final ColaboradorRepository colaboradorRepository;

    public ColaboradorUserDetailsService(ColaboradorRepository colaboradorRepository) {
        this.colaboradorRepository = colaboradorRepository;
    }

    /**
     * Carrega o utilizador autenticável pelo username informado no formulário de login.
     *
     * @param username username submetido
     * @return detalhes de autenticação esperados pelo Spring Security
     * @throws UsernameNotFoundException quando não existe colaborador com esse username
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Colaborador colaborador = colaboradorRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Colaborador não encontrado"));

        return User.withUsername(colaborador.getUsername())
            .password(colaborador.getPasswordHash())
            .roles(colaborador.getTipoColaborador().name())
            .disabled(!colaborador.isAtivo())
            .build();
    }
}
