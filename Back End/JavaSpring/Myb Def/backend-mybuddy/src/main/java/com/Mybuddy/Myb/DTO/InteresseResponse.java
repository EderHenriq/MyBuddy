package com.Mybuddy.Myb.DTO;

// Importa as classes dos modelos e enums necessários
import com.Mybuddy.Myb.Model.StatusInteresse;
import java.time.LocalDateTime;

/**
 * Record para detalhes do interesse de adoção, incluindo objetos completos de usuário e pet
 */
public record InteresseResponse(
        Long id,                        // Identificador único do interesse de adoção
        UsuarioResponse usuario,        // Objeto usuário, contendo id e nome
        PetResumoResponse pet,                // Objeto pet, contendo id e nome
        StatusInteresse status,         // Status atual do interesse
        String mensagem,                // Mensagem enviada com o interesse
        LocalDateTime criadoEm,         // Data/hora de criação
        LocalDateTime atualizadoEm      // Data/hora da última atualização
) {}

