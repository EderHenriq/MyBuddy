package com.Mybuddy.Myb.Dto; // Declara o pacote onde este DTO (Data Transfer Object) está localizado.

import com.Mybuddy.Myb.Model.StatusInteresse; // Importa a enumeração StatusInteresse, que define os possíveis status para um interesse de adoção.
import jakarta.validation.constraints.NotNull; // Importa a anotação de validação @NotNull, que garante que o campo não seja nulo.

// Declara um "record" Java, que é uma nova funcionalidade do Java 16+ para criar classes imutáveis de transporte de dados de forma concisa.
// Este record representa uma requisição para atualizar o status de um interesse de adoção.
public record AtualizarStatusRequest(
        @NotNull StatusInteresse status // Declara um campo 'status' do tipo StatusInteresse.
        // A anotação @NotNull indica que o valor para 'status' não pode ser nulo na requisição.
        // O record gera automaticamente construtor, getters, equals(), hashCode() e toString() para este campo.
) {}