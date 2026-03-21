package com.Mybuddy.Myb.DTO; // Declara o pacote onde esta classe DTO (Data Transfer Object) está localizada.

import com.Mybuddy.Myb.Model.InteresseAdocao; // Importa a entidade InteresseAdocao, que representa o interesse completo no banco.
import com.Mybuddy.Myb.DTO.UsuarioResponse;     // Importa o DTO simplificado do usuário (nome + id).
import com.Mybuddy.Myb.DTO.PetResumoResponse; //Importa o DTO simplificado

// Declara uma classe final chamada InteresseAdocaoMapper.
// Classes "final" não podem ser estendidas, reforçando o uso utilitário da classe.
public final class InteresseAdocaoMapper {
    // Construtor privado. Impede instanciação externa, reforçando o padrão utilitário.
    private InteresseAdocaoMapper() {}

    // Método estático que converte uma entidade InteresseAdocao em um DTO InteresseResponse.
    // Agora mapeia os objetos completos de usuário e pet, essenciais para o frontend exibir nomes.
    public static InteresseResponse toResponse(InteresseAdocao i) {
        return new InteresseResponse(
                i.getId(), // ID do interesse
                // Mapeia o objeto DTO do usuário (id e nome)
                new UsuarioResponse(
                        i.getUsuario().getId(),
                        i.getUsuario().getNome()
                ),
                // Mapeia o objeto DTO do pet (id e nome)
                new PetResumoResponse(
                        i.getPet().getId(),
                        i.getPet().getNome()
                ),

                i.getStatus(),       // Status do interesse (pendente, aprovado, etc.)
                i.getMensagem(),     // Mensagem enviada junto com o interesse
                i.getCriadoEm(),     // Data/hora de criação do interesse
                i.getAtualizadoEm()  // Data/hora da última atualização do interesse
        );
    }
}
