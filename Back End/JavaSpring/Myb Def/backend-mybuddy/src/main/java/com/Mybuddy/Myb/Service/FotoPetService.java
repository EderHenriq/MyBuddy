package com.Mybuddy.Myb.Service;

import com.Mybuddy.Myb.Model.Arquivo;
import com.Mybuddy.Myb.Repository.mongo.ArquivoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Serviço responsável pelo gerenciamento de imagens e arquivos enviado pelos usuários.
 * Persiste os bytes dos arquivos diretamente no MongoDB para garantir resiliência e alta disponibilidade.
 */
@Service
public class FotoPetService {

    private static final Logger log = LoggerFactory.getLogger(FotoPetService.class);

    private final ArquivoRepository arquivoRepository;

    public FotoPetService(ArquivoRepository arquivoRepository) {
        this.arquivoRepository = arquivoRepository;
    }

    /**
     * Salva um arquivo/foto no MongoDB.
     */
    public String storeFile(MultipartFile file) throws IOException {
        // Validar tipo de conteúdo (apenas imagens permitidas)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Apenas arquivos de imagem são permitidos.");
        }

        // Validar tamanho (máximo 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("O tamanho máximo permitido para o arquivo é 5MB.");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }
        // Gera um nome único para o arquivo
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            if (fileName.contains("..")) {
                throw new IOException("Nome de arquivo inválido: " + fileName);
            }

            Arquivo arquivo = Arquivo.builder()
                    .id(fileName)
                    .nomeOriginal(originalFileName)
                    .tipoConteudo(file.getContentType())
                    .dados(file.getBytes())
                    .build();

            arquivoRepository.save(arquivo);
            log.info("Arquivo salvo com sucesso no MongoDB com o nome: {}", fileName);
            return fileName;
        } catch (IOException ex) {
            log.error("Erro ao salvar o arquivo '{}' no MongoDB: {}", originalFileName, ex.getMessage(), ex);
            throw new IOException("Não foi possível armazenar o arquivo " + originalFileName + ". Por favor, tente novamente!", ex);
        }
    }

    /**
     * Salva múltiplos arquivos no MongoDB.
     */
    public List<String> storeFiles(List<MultipartFile> files) throws IOException {
        List<String> fileNames = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = storeFile(file);
                fileNames.add(fileName);
            }
        }
        log.info("Upload múltiplo concluído no MongoDB. {} arquivos persistidos.", fileNames.size());
        return fileNames;
    }
}