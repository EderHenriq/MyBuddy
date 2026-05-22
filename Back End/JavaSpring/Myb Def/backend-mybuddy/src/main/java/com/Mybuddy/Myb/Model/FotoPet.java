package com.Mybuddy.Myb.Model;

import lombok.*;

/**
 * Classe FotoPet adaptada para ser embutida diretamente na entidade Pet do MongoDB.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FotoPet {

    @EqualsAndHashCode.Include
    private Long id;

    private String url;

    private boolean principal;
}