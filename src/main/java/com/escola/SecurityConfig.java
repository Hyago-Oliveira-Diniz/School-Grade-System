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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/login", "/api/cadastro").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/turmas/**",
                                "/api/disciplinas/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/turmas/**",
                                "/api/disciplinas/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/turmas/**",
                                "/api/disciplinas/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/professores/**",
                                "/api/alunos/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/professores/**",
                                "/api/alunos/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/professores/**",
                                "/api/alunos/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,
                                "/api/notas/**",
                                "/api/frequencias/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")

                        .requestMatchers(HttpMethod.POST,
                                "/api/trabalhos/**")
                        .hasAnyRole("ADMIN", "PROFESSOR")

                        .requestMatchers(HttpMethod.GET,
                                "/api/turmas/**",
                                "/api/disciplinas/**",
                                "/api/alunos/**",
                                "/api/professores/**",
                                "/api/notas/**",
                                "/api/frequencias/**",
                                "/api/trabalhos/**")
                        .hasAnyRole("ADMIN", "PROFESSOR", "ALUNO", "RESPONSAVEL")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/trabalhos/**")
                        .hasRole("ADMIN")

                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
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