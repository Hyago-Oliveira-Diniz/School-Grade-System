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

    public Usuario cadastrar(Usuario usuarioInput) {
        // Campos extras que vêm do body mas não existem em Usuario
        String materia  = usuarioInput.getMateria();
        String registro = usuarioInput.getRegistro();
        String matricula = usuarioInput.getMatricula();

        if (usuarioRepository.findByUsuario(usuarioInput.getUsuario()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe");
        }

        usuarioInput.setSenha(encoder.encode(usuarioInput.getSenha()));

        Usuario usuarioSalvo;
        try {
            usuarioSalvo = usuarioRepository.save(usuarioInput);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao cadastrar usuário");
        }

        // Cria automaticamente o perfil vinculado conforme o tipo
        if (usuarioSalvo.getTipo() == TipoUsuario.ALUNO) {
            Aluno aluno = new Aluno();
            aluno.setNome(usuarioSalvo.getNome());
            aluno.setUsuario(usuarioSalvo);
            aluno.setMatricula(matricula != null ? matricula : "");
            alunoRepository.save(aluno);

        } else if (usuarioSalvo.getTipo() == TipoUsuario.PROFESSOR) {
            Professor professor = new Professor();
            professor.setNome(usuarioSalvo.getNome());
            professor.setUsuario(usuarioSalvo);
            professor.setMateria(materia != null ? materia : "");
            professor.setRegistro(registro != null ? registro : "");
            professorRepository.save(professor);
        }

        return usuarioSalvo;
    }

    public Usuario autenticar(String nomeUsuario, String senhaRaw) {
        Usuario usuario = usuarioRepository.findByUsuario(nomeUsuario);
        if (usuario == null || !encoder.matches(senhaRaw, usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos");
        }
        return usuario;
    }
}
