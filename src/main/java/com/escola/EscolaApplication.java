package com.escola;

import com.escola.dto.UsuarioRegistroDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class EscolaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EscolaApplication.class, args);
    }

    // Este Bean roda automaticamente toda vez que o servidor sobe
    @Bean
    public CommandLineRunner criarAdminPadrao(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        return args -> {
            // Verifica se o usuário "admin" já existe no banco
            if (usuarioRepository.findByUsuario("admin") == null) {

                UsuarioRegistroDTO adminDto = new UsuarioRegistroDTO();
                adminDto.setNome("Administrador do Sistema");
                adminDto.setUsuario("admin");
                adminDto.setSenha("123");
                adminDto.setTipo(TipoUsuario.ADMIN);

                usuarioService.cadastrar(adminDto);

                System.out.println("=================================================");
                System.out.println("✅ USUÁRIO ADMIN PADRÃO CRIADO COM SUCESSO!");
                System.out.println("Login: admin | Senha: 123");
                System.out.println("=================================================");
            }
        };
    }
}