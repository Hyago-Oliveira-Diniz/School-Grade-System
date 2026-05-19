package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Endpoints públicos — qualquer um acessa
                .requestMatchers("/api/login", "/api/cadastro").permitAll()

                // Apenas ADMIN pode gerenciar usuários
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // ADMIN e PROFESSOR podem criar/editar turmas e disciplinas
                .requestMatchers(HttpMethod.POST, "/api/turmas/**", "/api/disciplinas/**")
                    .hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.PUT, "/api/turmas/**", "/api/disciplinas/**")
                    .hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.DELETE, "/api/turmas/**", "/api/disciplinas/**")
                    .hasRole("ADMIN")

                // ADMIN pode criar/editar professores e alunos
                .requestMatchers(HttpMethod.POST, "/api/professores/**", "/api/alunos/**")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/professores/**", "/api/alunos/**")
                    .hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.DELETE, "/api/professores/**", "/api/alunos/**")
                    .hasRole("ADMIN")

                // ALUNO e RESPONSAVEL só podem consultar (GET)
                .requestMatchers(HttpMethod.GET, "/api/turmas/**", "/api/disciplinas/**",
                        "/api/alunos/**", "/api/professores/**")
                    .hasAnyRole("ADMIN", "PROFESSOR", "ALUNO", "RESPONSAVEL")

                // Qualquer outro endpoint exige login
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
