package com.Mybuddy.Myb.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "campanhas_doacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CampanhaDoacao {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String titulo;
    private String descricao;
    private BigDecimal meta;
    private BigDecimal arrecadado;
    
    private Long petId; // Opcional - se a campanha for para um pet específico
    private Long organizacaoId; // Obrigatório - a ONG dona da campanha
    
    private String categoria; // CIRURGIA, RACAO, TRATAMENTO, GERAL
    private LocalDateTime dataExpiracao;
    private String status; // ATIVA, ENCERRADA, META_ATINGIDA

    @CreatedDate
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    private LocalDateTime dataAtualizacao;
}
