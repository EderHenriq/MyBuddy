package com.Mybuddy.Myb.Dto; // Declara o pacote onde este DTO (Data Transfer Object) está localizado.

import jakarta.validation.constraints.NotNull; // Importa a anotação de validação @NotNull, que garante que o campo não seja nulo.
import jakarta.validation.constraints.Size; // Importa a anotação de validação @Size, que verifica o tamanho de uma string ou coleção.

// Declara um "record" Java, que é uma nova funcionalidade do Java 16+ para criar classes imutáveis de transporte de dados de forma concisa.
// Este record representa a estrutura de dados esperada no corpo de uma requisição HTTP
// para registrar um novo interesse de adoção.
public record RegistrarInteresseRequest(
        @NotNull Long usuarioId, // Declara um campo 'usuarioId' do tipo Long.
        // A anotação @NotNull indica que este ID não pode ser nulo na requisição.
        @NotNull Long petId, // Declara um campo 'petId' do tipo Long.
        // A anotação @NotNull indica que este ID não pode ser nulo na requisição.
        @Size(max = 500) String mensagem // Declara um campo 'mensagem' do tipo String.
        // A anotação @Size(max = 500) indica que a mensagem pode ter no máximo 500 caracteres.
        // A mensagem é opcional, pois não tem @NotNull, mas se presente, deve respeitar o tamanho.
        // O record gera automaticamente construtor, getters, equals(), hashCode() e toString() para todos estes campos.
) {}