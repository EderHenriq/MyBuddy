package com.Mybuddy.Myb.Dto; // Declara o pacote onde este DTO (Data Transfer Object) está localizado.

import jakarta.validation.constraints.NotNull; // Importa a anotação de validação @NotNull, que garante que o campo não seja nulo.
import jakarta.validation.constraints.Size; // Importa a anotação de validação @Size, que verifica o tamanho de uma string ou coleção.

// Declara um "record" Java, que é uma nova funcionalidade do Java 16+ para criar classes imutáveis de transporte de dados de forma concisa.
// Este record representa a estrutura de dados esperada no corpo de uma requisição HTTP
// para manifestar interesse em adotar um pet (BUDDY-77).
// O usuarioId não é necessário aqui, pois é extraído automaticamente do token JWT no Controller.
public record RegistrarInteresseRequest(

        @NotNull(message = "O ID do pet é obrigatório") // Declara um campo 'petId' do tipo Long.
        // A anotação @NotNull indica que este ID não pode ser nulo na requisição.
        Long petId, // ID do pet no qual o usuário deseja manifestar interesse

        @Size(max = 500, message = "A mensagem não pode exceder 500 caracteres") // Declara um campo 'mensagem' do tipo String.
        // A anotação @Size(max = 500) indica que a mensagem pode ter no máximo 500 caracteres.
        String mensagem // Mensagem opcional explicando o motivo do interesse (pode ser null, pois não tem @NotNull)

        // O record gera automaticamente construtor, getters, equals(), hashCode() e toString() para todos estes campos.
) {}
