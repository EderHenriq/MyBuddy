package com.Mybuddy.Myb.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

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
}
