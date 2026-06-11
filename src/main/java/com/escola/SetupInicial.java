package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SetupInicial implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Traz o criptografador de senhas do seu SecurityConfig
    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // O sistema verifica se o banco de usuários está vazio
        if (usuarioRepository.count() == 0) {

            Usuario admin = new Usuario();
            admin.setNome("Administrador do Sistema");
            admin.setUsuario("admin");

            // AQUI ESTÁ A CORREÇÃO: Usando o seu Enum TipoUsuario
            admin.setTipo(TipoUsuario.ADMIN);

            // Criptografa a senha "123" para o login do JWT aceitar
            if (passwordEncoder != null) {
                admin.setSenha(passwordEncoder.encode("123"));
            } else {
                admin.setSenha("123");
            }

            usuarioRepository.save(admin);
            System.out.println("✅ MÁGICA: Usuário ADMIN (admin / 123) restaurado automaticamente!");
        }
    }
}