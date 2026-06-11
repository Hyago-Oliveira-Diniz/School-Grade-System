package com.escola;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsaveis")
@CrossOrigin
public class ResponsavelController {

    @Autowired
    private ResponsavelRepository responsavelRepository;

    // Retorna a lista de responsáveis para o Painel Administrativo
    @GetMapping
    public List<Responsavel> listar() {
        return responsavelRepository.findAll();
    }
}