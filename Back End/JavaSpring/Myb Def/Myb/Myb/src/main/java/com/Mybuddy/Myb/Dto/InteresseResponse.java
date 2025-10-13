package com.Mybuddy.Myb.Dto; // Declara o pacote onde este DTO (Data Transfer Object) está localizado.

import com.Mybuddy.Myb.Model.StatusInteresse; // Importa a enumeração StatusInteresse, que define os possíveis status para um interesse de adoção.

import java.time.LocalDateTime; // Importa a classe LocalDateTime para lidar com datas e horas.

// Declara um "record" Java, que é uma nova funcionalidade do Java 16+ para criar classes imutáveis de transporte de dados de forma concisa.
// Este record representa a estrutura de dados que será retornada como resposta (geralmente em JSON)
// para o cliente da API quando informações sobre um interesse de adoção forem solicitadas.
public record InteresseResponse(
        Long id, // O identificador único do interesse de adoção.
        Long usuarioId, // O identificador do usuário que manifestou o interesse.
        Long petId, // O identificador do pet que recebeu o interesse.
        StatusInteresse status, // O status atual do interesse (ex: PENDENTE, APROVADO, REJEITADO).
        String mensagem, // A mensagem que foi enviada pelo usuário junto com o interesse.
        LocalDateTime criadoEm, // A data e hora em que o interesse foi criado.
        LocalDateTime atualizadoEm // A data e hora da última atualização do interesse.
        // O record gera automaticamente construtor, getters, equals(), hashCode() e toString() para todos estes campos.
) {}