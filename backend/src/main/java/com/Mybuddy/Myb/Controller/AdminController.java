package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Repository.mongo.OrganizacaoRepository;
import com.Mybuddy.Myb.Repository.mongo.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@SuppressWarnings("null")
public class AdminController {

    private final OrganizacaoRepository organizacaoRepository;
    private final UsuarioRepository usuarioRepository;

    @GetMapping("/ongs")
    public ResponseEntity<List<Organizacao>> getOngs() {
        return ResponseEntity.ok(organizacaoRepository.findAll());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getUsuarios() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @GetMapping("/denuncias")
    public ResponseEntity<List<Object>> getDenuncias() {
        return ResponseEntity.ok(Collections.emptyList());
    }

    @GetMapping("/tickets")
    public ResponseEntity<List<Object>> getTickets() {
        return ResponseEntity.ok(Collections.emptyList());
    }
}
