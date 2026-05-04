package pt.hotel.animais.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Permite iframe para consola H2 (apenas desenvolvimento)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        // Utilizadores de teste — serão substituídos por UserDetailsService com BD
        var diretor = User.withUsername("diretor")
            .password(encoder.encode("diretor123"))
            .roles("DIRETOR")
            .build();
        var recepcao = User.withUsername("recepcao")
            .password(encoder.encode("recepcao123"))
            .roles("FUNCIONARIO_RECEPCAO")
            .build();
        var cuidador = User.withUsername("cuidador")
            .password(encoder.encode("cuidador123"))
            .roles("CUIDADOR")
            .build();
        var veterinario = User.withUsername("veterinario")
            .password(encoder.encode("vet123"))
            .roles("MEDICO_VETERINARIO")
            .build();
        var limpeza = User.withUsername("limpeza")
            .password(encoder.encode("limpeza123"))
            .roles("RESPONSAVEL_LIMPEZA")
            .build();
        return new InMemoryUserDetailsManager(diretor, recepcao, cuidador, veterinario, limpeza);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
