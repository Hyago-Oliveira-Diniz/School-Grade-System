package com.escola;

import com.escola.dto.UsuarioRegistroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AlunoRepository alunoRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private ResponsavelRepository responsavelRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        }
        return usuario;
    }

    public Usuario cadastrar(UsuarioRegistroDTO dto) {
        if (usuarioRepository.findByUsuario(dto.getUsuario()) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setUsuario(dto.getUsuario());
        novoUsuario.setNome(dto.getNome());
        novoUsuario.setTipo(dto.getTipo());
        novoUsuario.setSenha(encoder.encode(dto.getSenha()));

        Usuario usuarioSalvo;
        try {
            usuarioSalvo = usuarioRepository.save(novoUsuario);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao cadastrar usuário");
        }

        if (dto.getTipo() == TipoUsuario.ALUNO) {
            Aluno aluno = new Aluno();
            aluno.setNome(usuarioSalvo.getNome());
            aluno.setUsuario(usuarioSalvo);
            aluno.setMatricula(dto.getMatricula() != null ? dto.getMatricula() : "");
            alunoRepository.save(aluno);

        } else if (dto.getTipo() == TipoUsuario.PROFESSOR) {
            Professor professor = new Professor();
            professor.setNome(usuarioSalvo.getNome());
            professor.setUsuario(usuarioSalvo);
            professor.setMateria(dto.getMateria() != null ? dto.getMateria() : "");
            professor.setRegistro(dto.getRegistro() != null ? dto.getRegistro() : "");
            professorRepository.save(professor);

        } else if (dto.getTipo() == TipoUsuario.RESPONSAVEL) {
            Responsavel responsavel = new Responsavel();
            responsavel.setNome(usuarioSalvo.getNome());
            responsavel.setUsuario(usuarioSalvo);
            responsavel.setRg(dto.getRg());
            responsavel.setTelefone(dto.getTelefone());

            if (dto.getMatriculasAlunos() != null) {
                List<Aluno> filhos = dto.getMatriculasAlunos().stream()
                        .map(mat -> alunoRepository.findByMatricula(mat))
                        .filter(a -> a != null)
                        .toList();
                responsavel.setAlunos(filhos);
            }
            responsavelRepository.save(responsavel);
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