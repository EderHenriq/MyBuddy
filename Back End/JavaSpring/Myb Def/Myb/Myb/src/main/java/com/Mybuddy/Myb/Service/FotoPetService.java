package com.Mybuddy.Myb.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FotoPetService {

    private static final Logger log = LoggerFactory.getLogger(FotoPetService.class);

    private final Path fileStorageLocation;

    public FotoPetService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("Diretório de upload de arquivos criado em: {}", this.fileStorageLocation);
        } catch (Exception ex) {
            log.error("Não foi possível criar o diretório de upload: {}", this.fileStorageLocation, ex);
            throw new RuntimeException("Não foi possível criar o diretório de upload.", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }
        String fileName = UUID.randomUUID().toString() + fileExtension; // Gera um nome único

        try {
            // Verificar se o nome do arquivo contém caracteres inválidos
            if (fileName.contains("..")) {
                throw new IOException("Nome do arquivo inválido: " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Arquivo salvo com sucesso: {}", targetLocation.toString());
            return fileName; // Retorna o nome do arquivo salvo no servidor
        } catch (IOException ex) {
            log.error("Erro ao salvar o arquivo '{}': {}", originalFileName, ex.getMessage(), ex);
            throw new IOException("Não foi possível armazenar o arquivo " + originalFileName + ". Por favor, tente novamente!", ex);
        }
    }

    // Você pode adicionar outros métodos para carregar/deletar arquivos, se necessário
    public Path loadFileAsResource(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }
}