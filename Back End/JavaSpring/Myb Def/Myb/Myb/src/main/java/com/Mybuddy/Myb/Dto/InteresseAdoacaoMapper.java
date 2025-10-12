package com.Mybuddy.Myb.Dto; // Declara o pacote onde esta classe DTO (Data Transfer Object) está localizada.

import com.Mybuddy.Myb.Model.InteresseAdoacao; // Importa a entidade (modelo) InteresseAdoacao, que representa um interesse de adoção completo no sistema.

// Declara uma classe final chamada InteresseAdoacaoMapper.
// Classes "final" não podem ser estendidas. Isso sugere que ela é uma classe utilitária.
// O nome "Mapper" indica que seu propósito é mapear ou converter objetos de um tipo para outro.
public final class InteresseAdoacaoMapper {
    // Construtor privado. Isso impede que instâncias desta classe sejam criadas.
    // Isso reforça a ideia de que é uma classe utilitária com métodos estáticos.
    private InteresseAdoacaoMapper() {}

    // Método estático público que converte um objeto InteresseAdoacao (entidade do banco de dados)
    // para um objeto InteresseResponse (DTO de resposta).
    // O objetivo é expor apenas os dados relevantes e no formato desejado para o cliente da API.
    public static InteresseResponse toResponse(InteresseAdoacao i) {
        // Cria e retorna uma nova instância de InteresseResponse,
        // preenchendo seus campos com os dados correspondentes do objeto InteresseAdoacao (i).
        return new InteresseResponse(
                i.getId(), // Mapeia o ID do interesse
                i.getUsuario().getId(), // Mapeia o ID do usuário (quem manifestou interesse)
                i.getPet().getId(), // Mapeia o ID do pet (o pet que recebeu o interesse)
                i.getStatus(), // Mapeia o status do interesse (pendente, aprovado, etc.)
                i.getMensagem(), // Mapeia a mensagem enviada junto com o interesse
                i.getCriadoEm(), // Mapeia a data/hora de criação do interesse
                i.getAtuaziladoEm() // Mapeia a data/hora da última atualização do interesse
        );
    }
}