package com.Mybuddy.Myb.Controller;

import com.Mybuddy.Myb.Repository.mongo.ArquivoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SuppressWarnings("null")
public class UploadsController {

    private static final Logger log = LoggerFactory.getLogger(UploadsController.class);

    private final ArquivoRepository arquivoRepository;

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) {
        log.info("Requisição recebida para baixar o arquivo: {}", filename);
        return arquivoRepository.findById(filename)
                .map(arquivo -> {
                    MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    if (arquivo.getTipoConteudo() != null) {
                        try {
                            mediaType = MediaType.parseMediaType(arquivo.getTipoConteudo());
                        } catch (Exception e) {
                            // Ignora erro de parsing e utiliza tipo genérico
                        }
                    }
                    return ResponseEntity.ok()
                            .contentType(mediaType)
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + arquivo.getNomeOriginal() + "\"")
                            .body(arquivo.getDados());
                })
                .orElseGet(() -> {
                    log.warn("Arquivo não encontrado no MongoDB: {}", filename);
                    return ResponseEntity.notFound().build();
                });
    }
}
