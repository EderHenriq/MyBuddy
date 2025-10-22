package com.Mybuddy.Myb.Controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Mybuddy.Myb.Model.Organizacao;
import com.Mybuddy.Myb.Service.OrganizacaoService;

//julia linda a mais mais

@RestController
@RequestMapping("/api/organizacoes")
public class OrganizacaoController {
     private final OrganizacaoService service;

    public OrganizacaoController(OrganizacaoService service) {
        this.service = service;
    }

    //listar todas
    @GetMapping
    public List<Organizacao> listarTodas() {
        return service.listarTodas();
    }

    //buscar por id
    @GetMapping("/{id}")
    public ResponseEntity<Organizacao> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //criar nova
    @PostMapping
    public Organizacao criar(@RequestBody Organizacao ong) {
        return service.salvar(ong);
    }

    //aualizar existente
    @PutMapping("/{id}")
    public ResponseEntity<Organizacao> atualizar(@PathVariable Long id, @RequestBody Organizacao ongAtualizada) {
        return service.buscarPorId(id)
                .map(ong -> {
                    ong.setNome(ongAtualizada.getNome());
                    ong.setEmail(ongAtualizada.getEmail());
                    ong.setTelefone(ongAtualizada.getTelefone());
                    ong.setEndereco(ongAtualizada.getEndereco());
                    return ResponseEntity.ok(service.salvar(ong));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    //deletar porrraaaaaaaaaaaaaaaaaaaaaaaaaaaaaa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ong -> {
                    service.deletar(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
