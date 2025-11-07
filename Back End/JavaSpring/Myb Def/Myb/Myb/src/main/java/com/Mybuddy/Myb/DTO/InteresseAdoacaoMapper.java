package com.Mybuddy.Myb.DTO; // Declara o pacote onde esta classe DTO (Data Transfer Object) está localizada.

import com.Mybuddy.Myb.Model.InteresseAdoacao; // Importa a entidade InteresseAdoacao, que representa o interesse completo no banco.
import com.Mybuddy.Myb.DTO.UsuarioResponse;     // Importa o DTO simplificado do usuário (nome + id).
import com.Mybuddy.Myb.DTO.PetResumoResponse; //Importa o DTO simplificado

// Declara uma classe final chamada InteresseAdoacaoMapper.
// Classes "final" não podem ser estendidas, reforçando o uso utilitário da classe.
public final class InteresseAdoacaoMapper {
    // Construtor privado. Impede instanciação externa, reforçando o padrão utilitário.
    private InteresseAdoacaoMapper() {}

    // Método estático que converte uma entidade InteresseAdoacao em um DTO InteresseResponse.
    // Agora mapeia os objetos completos de usuário e pet, essenciais para o frontend exibir nomes.
    public static InteresseResponse toResponse(InteresseAdoacao i) {
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
