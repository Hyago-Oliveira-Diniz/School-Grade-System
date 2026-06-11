package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        return usuario;
    }

    public Usuario cadastrar(Usuario usuario) {
        if (usuarioRepository.findByUsuario(usuario.getUsuario()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe");
        }

        // Hash da senha
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        try {
            Usuario salvo = usuarioRepository.save(usuario);

            // Cria o registro na tabela correta baseado no tipo
            if (salvo.getTipo() == TipoUsuario.ALUNO) {
                Aluno aluno = new Aluno();
                aluno.setNome(salvo.getNome());
                aluno.setUsuario(salvo);
                alunoRepository.save(aluno);
            } else if (salvo.getTipo() == TipoUsuario.PROFESSOR) {
                Professor professor = new Professor();
                professor.setNome(salvo.getNome());
                professor.setUsuario(salvo);
                professorRepository.save(professor);
            }

            return salvo;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao cadastrar usuário");
        }
    }

    public Usuario autenticar(String nomeUsuario, String senhaRaw) {
        Usuario usuario = usuarioRepository.findByUsuario(nomeUsuario);
        if (usuario == null || !encoder.matches(senhaRaw, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }
        return usuario;
    }
}
