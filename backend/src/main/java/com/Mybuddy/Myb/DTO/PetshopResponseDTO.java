package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusAprovacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO de resposta de Petshop.
 * Inclui statusAprovacao para que admins e o próprio petshop acompanhem
 * o status de aprovação na plataforma.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetshopResponseDTO {
    private Long id;
    private String nomeFantasia;
    private String emailContato;
    private String cnpj;
    private String telefoneContato;
    private String endereco;
    private String descricao;
    private String website;
    private BigDecimal valorMinimoFreteGratis;
    private StatusAprovacao statusAprovacao;
}

