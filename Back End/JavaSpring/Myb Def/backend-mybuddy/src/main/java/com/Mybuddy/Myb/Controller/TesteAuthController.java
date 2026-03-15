package com.Mybuddy.Myb.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// IMPORTANTE: Remova ou não adicione @CrossOrigin aqui se já configurou CORS globalmente
// @CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500"}, maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/api/teste") // Este é o prefixo da URL para este controller
public class TesteAuthController {

    // Este endpoint requer QUALQUER usuário autenticado
    @GetMapping("/autenticado") // A URL completa será /api/teste/autenticado
    @PreAuthorize("isAuthenticated()") // Simplesmente verifica se o usuário está logado
    public String getMensagemAutenticada() {
        return "Parabéns! Você está autenticado e acessou este recurso!";
    }

    // Este endpoint requer um usuário com a role ONG_USER ou ONG_ADMIN
    // O usuário 'onguses' (que você logou) tem a role 'ONG_USER'
    @GetMapping("/usuario") // A URL completa será /api/teste/usuario
    @PreAuthorize("hasRole('ONG_USER') or hasRole('ONG_ADMIN')")
    public String getMensagemUsuario() {
        return "Olá, usuário ONG! Você tem permissão para ver isso.";
    }

    // Este endpoint requer um usuário com a role ONG_ADMIN
    @GetMapping("/admin") // A URL completa será /api/teste/admin
    @PreAuthorize("hasRole('ONG_ADMIN')")
    public String getMensagemAdmin() {
        return "Atenção administradores! Conteúdo secreto aqui.";
    }
}