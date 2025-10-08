package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Service.PetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping
    public ResponseEntity<Pet> criar(@RequestBody Pet pet) {
        Pet criado = petService.criarPet(pet);
        return ResponseEntity.created(URI.create("/api/pets/" + criado.getId())).body(criado);
    }

    @GetMapping
    public ResponseEntity<List<Pet>> listar() {
        return ResponseEntity.ok(petService.buscarTodosPets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> buscarPorId(@PathVariable Long id) {
        return petService.buscarPetPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> atualizar(@PathVariable Long id, @RequestBody Pet pet) {
        Pet atualizado = petService.atualizarPet(id, pet);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petService.deletarPet(id);
        return ResponseEntity.noContent().build();
    }
}

