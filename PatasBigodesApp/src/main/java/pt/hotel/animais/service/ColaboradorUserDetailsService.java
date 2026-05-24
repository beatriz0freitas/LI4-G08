package pt.hotel.animais.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.hotel.animais.model.Colaborador;
import pt.hotel.animais.repository.ColaboradorRepository;

@Service
public class ColaboradorUserDetailsService implements UserDetailsService {

    private final ColaboradorRepository colaboradorRepository;

    public ColaboradorUserDetailsService(ColaboradorRepository colaboradorRepository) {
        this.colaboradorRepository = colaboradorRepository;
    }

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
