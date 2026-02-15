package com.Mybuddy.Myb.Dto;

import com.Mybuddy.Myb.Model.InteresseAdocao;
import com.Mybuddy.Myb.Model.Pet;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Model.StatusInteresse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InteresseAdocaoMapperTest {

    @Test
    public void toResponse_mapsAllFieldsCorrectly() {
        // arrange: create modelo objects
        //Usuario usuario = new Usuario("Maria", "maria@example.com", "12345");
        //usuario.setId(42L);

        Pet pet = new Pet();
        pet.setId(99L);
        pet.setNome("Rex");

        InteresseAdocao interesse = new InteresseAdocao();
        interesse.setId(7L);
        interesse.setUsuario(usuario);
        interesse.setPet(pet);
        interesse.setStatus(StatusInteresse.PENDENTE);
        interesse.setMensagem("Quero adotar");
        LocalDateTime now = LocalDateTime.now();
        interesse.setCriadoEm(now);
        interesse.setAtualizadoEm(now.plusHours(1));

        // act
        InteresseResponse resp = InteresseAdocaoMapper.toResponse(interesse);

        // assert
        assertNotNull(resp);
        assertEquals(7L, resp.id());
        assertNotNull(resp.usuario());
        assertEquals(42L, resp.usuario().id());
        assertEquals("Maria", resp.usuario().nome());
        assertNotNull(resp.pet());
        assertEquals(99L, resp.pet().id());
        assertEquals("Rex", resp.pet().nome());
        assertEquals(StatusInteresse.PENDENTE, resp.status());
        assertEquals("Quero adotar", resp.mensagem());
        assertEquals(now, resp.criadoEm());
        assertEquals(now.plusHours(1), resp.atualizadoEm());
    }
}
