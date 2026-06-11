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
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth

                // Públicos
                .requestMatchers("/api/login", "/api/cadastro").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Só ADMIN vê todos os usuários
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // Professor e Admin criam/editam trabalhos, avaliações, advertências, frequências
                .requestMatchers(HttpMethod.POST,
                    "/api/trabalhos/**", "/api/avaliacoes/**",
                    "/api/advertencias/**", "/api/frequencias/**",
                    "/api/notas/**", "/api/turmas/**", "/api/disciplinas/**")
                    .hasAnyRole("ADMIN", "PROFESSOR")

                .requestMatchers(HttpMethod.PUT,
                    "/api/trabalhos/**", "/api/turmas/**", "/api/disciplinas/**")
                    .hasAnyRole("ADMIN", "PROFESSOR")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/trabalhos/**", "/api/avaliacoes/**", "/api/advertencias/**")
                    .hasAnyRole("ADMIN", "PROFESSOR")

                .requestMatchers(HttpMethod.DELETE,
                    "/api/turmas/**", "/api/disciplinas/**",
                    "/api/alunos/**", "/api/professores/**")
                    .hasRole("ADMIN")

                // Leitura — todos os perfis logados
                .requestMatchers(HttpMethod.GET,
                    "/api/turmas/**", "/api/disciplinas/**",
                    "/api/alunos/**", "/api/professores/**",
                    "/api/notas/**", "/api/frequencias/**",
                    "/api/trabalhos/**", "/api/avaliacoes/**",
                    "/api/advertencias/**")
                    .hasAnyRole("ADMIN", "PROFESSOR", "ALUNO", "RESPONSAVEL")

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
