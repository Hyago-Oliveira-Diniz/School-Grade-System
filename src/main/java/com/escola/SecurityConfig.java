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

                        // Endpoints totalmente públicos
                        .requestMatchers("/api/login", "/api/cadastro").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // Apenas ADMIN gerencia usuários
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        // ADMIN e PROFESSOR criam/editam turmas e disciplinas
                        .requestMatchers(HttpMethod.POST, "/api/turmas/**", "/api/disciplinas/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.PUT, "/api/turmas/**", "/api/disciplinas/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/turmas/**", "/api/disciplinas/**")
                        .hasRole("ADMIN")

                        // ADMIN gerencia professores e alunos
                        .requestMatchers(HttpMethod.POST, "/api/professores/**", "/api/alunos/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/professores/**", "/api/alunos/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/professores/**", "/api/alunos/**")
                        .hasRole("ADMIN")

                        // Lançar notas e frequência — PROFESSOR e ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/notas/**", "/api/frequencias/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")

                        // Consultas — todos os perfis logados podem ver
                        .requestMatchers(HttpMethod.GET, "/api/turmas/**", "/api/disciplinas/**",
                                "/api/alunos/**", "/api/professores/**",
                                "/api/notas/**", "/api/frequencias/**")
                        .hasAnyRole("ADMIN", "PROFESSOR", "ALUNO", "RESPONSAVEL")

                        // Qualquer outra rota exige autenticação
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