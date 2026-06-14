package com.Mybuddy.Myb.Controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teste")
public class TesteAuthController {

    @GetMapping("/autenticado")
    @PreAuthorize("isAuthenticated()")
    public String getMensagemAutenticada() {
        return "Você está autenticado!";
    }

    @GetMapping("/ong")
    @PreAuthorize("hasRole('ONG')")
    public String getMensagemOng() {
        return "Você é uma ONG!";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String getMensagemAdmin() {
        return "Você é um Admin!";
    }

    @GetMapping("/adotante")
    @PreAuthorize("hasRole('ADOTANTE')")
    public String getMensagemAdotante() {
        return "Você é um Adotante!";
    }
}