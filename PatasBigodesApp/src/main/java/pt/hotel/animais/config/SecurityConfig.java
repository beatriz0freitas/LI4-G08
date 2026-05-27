package pt.hotel.animais.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/relatorios", "/relatorios/**", "/colaboradores", "/colaboradores/**", "/auditoria", "/auditoria/**", "/admin/**").hasRole("DIRETOR")
                .requestMatchers("/dashboard").hasRole("DIRETOR")
                .requestMatchers("/tutores/**", "/animais/**", "/reservas/**", "/estadias/**", "/pagamentos", "/pagamentos/**")
                    .hasAnyRole("DIRETOR", "FUNCIONARIO_RECEPCAO")
                .requestMatchers("/cuidados/**", "/extras/**")
                    .hasAnyRole("DIRETOR", "CUIDADOR", "MEDICO_VETERINARIO")
                .requestMatchers("/plano-cuidados/**")
                    .hasAnyRole("DIRETOR", "FUNCIONARIO_RECEPCAO", "CUIDADOR", "MEDICO_VETERINARIO")
                .requestMatchers("/clinica", "/clinica/**")
                    .hasAnyRole("DIRETOR", "MEDICO_VETERINARIO")
                .requestMatchers("/notas/**")
                    .hasAnyRole("DIRETOR", "FUNCIONARIO_RECEPCAO", "CUIDADOR", "MEDICO_VETERINARIO")
                .requestMatchers("/historico/**")
                    .hasAnyRole("DIRETOR", "FUNCIONARIO_RECEPCAO", "MEDICO_VETERINARIO")
                .requestMatchers("/alojamentos", "/alojamentos/**")
                    .hasAnyRole("DIRETOR", "FUNCIONARIO_RECEPCAO")
                .requestMatchers("/limpeza", "/limpeza/**")
                    .hasAnyRole("DIRETOR", "RESPONSAVEL_LIMPEZA")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
