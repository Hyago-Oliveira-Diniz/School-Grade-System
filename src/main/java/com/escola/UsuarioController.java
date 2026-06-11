package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/cadastro")
    public Usuario cadastrar(@RequestBody Usuario usuario) {
        return usuarioService.cadastrar(usuario);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Usuario login) {
        Usuario usuario = usuarioService.autenticar(login.getUsuario(), login.getSenha());
        String token = jwtService.gerarToken(usuario);

        // Retorna id junto com os demais dados — necessário para o frontend buscar notas/frequências
        return Map.of(
            "token",   token,
            "id",      usuario.getId(),
            "tipo",    usuario.getTipo(),
            "nome",    usuario.getNome(),
            "usuario", usuario.getUsuario()
        );
    }

    // Apenas ADMIN acessa — protegido pelo SecurityConfig
    @GetMapping("/usuarios")
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }
}
