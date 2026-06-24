package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Config.SecurityConfig;
import com.Mybuddy.Myb.Model.Arquivo;
import com.Mybuddy.Myb.Repository.mongo.ArquivoRepository;
import com.Mybuddy.Myb.Security.JwtAuthConverter;
import com.Mybuddy.Myb.Service.KeycloakUserSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UploadsController.class)
@Import({SecurityConfig.class, JwtAuthConverter.class})
class UploadsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ArquivoRepository arquivoRepository;

    @MockitoBean
    private KeycloakUserSyncService keycloakUserSyncService;

    // ===================== GET /uploads/{filename} — público =====================

    @Test
    void serveFile_ArquivoEncontrado_DeveRetornar200ComConteudo() throws Exception {
        Arquivo arquivo = Arquivo.builder()
                .id("foto.jpg")
                .nomeOriginal("foto.jpg")
                .tipoConteudo("image/jpeg")
                .dados("conteudo fake".getBytes())
                .build();

        when(arquivoRepository.findById("foto.jpg")).thenReturn(Optional.of(arquivo));

        mockMvc.perform(get("/uploads/foto.jpg"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"foto.jpg\""));
    }

    @Test
    void serveFile_ArquivoNaoEncontrado_DeveRetornar404() throws Exception {
        when(arquivoRepository.findById("inexistente.jpg")).thenReturn(Optional.empty());

        mockMvc.perform(get("/uploads/inexistente.jpg"))
                .andExpect(status().isNotFound());
    }

    @Test
    void serveFile_SemTipoConteudo_DeveUsarOctetStream() throws Exception {
        Arquivo arquivo = Arquivo.builder()
                .id("arquivo.bin")
                .nomeOriginal("arquivo.bin")
                .tipoConteudo(null)
                .dados(new byte[]{1, 2, 3})
                .build();

        when(arquivoRepository.findById("arquivo.bin")).thenReturn(Optional.of(arquivo));

        mockMvc.perform(get("/uploads/arquivo.bin"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/octet-stream"));
    }
}
