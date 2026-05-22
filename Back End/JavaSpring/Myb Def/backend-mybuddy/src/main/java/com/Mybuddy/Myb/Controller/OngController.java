package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Model.EventoOng;
import com.Mybuddy.Myb.Model.InteresseAdocao;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Repository.mongo.EventoOngRepository;
import com.Mybuddy.Myb.Repository.mongo.InteresseAdocaoRepository;
import com.Mybuddy.Myb.Repository.mongo.PetRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ong")
public class OngController {

    private final InteresseAdocaoRepository interesseAdocaoRepository;
    private final EventoOngRepository eventoOngRepository;
    private final PetRepository petRepository;

    public OngController(InteresseAdocaoRepository interesseAdocaoRepository, 
                         EventoOngRepository eventoOngRepository, 
                         PetRepository petRepository) {
        this.interesseAdocaoRepository = interesseAdocaoRepository;
        this.eventoOngRepository = eventoOngRepository;
        this.petRepository = petRepository;
    }

    @GetMapping("/solicitacoes")
    public ResponseEntity<List<InteresseAdocao>> getSolicitacoes() {
        return ResponseEntity.ok(interesseAdocaoRepository.findAll());
    }

    @GetMapping("/eventos")
    public ResponseEntity<List<EventoOng>> getEventos() {
        return ResponseEntity.ok(eventoOngRepository.findAll());
    }

    @GetMapping("/pets")
    public ResponseEntity<List<Pet>> getPets() {
        return ResponseEntity.ok(petRepository.findAll());
    }
}
