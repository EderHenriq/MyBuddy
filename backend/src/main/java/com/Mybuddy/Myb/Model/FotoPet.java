package com.Mybuddy.Myb.Model;

import lombok.*;

/**
 * Foto de um pet armazenada no MongoDB junto ao documento do pet, indicando se é a foto principal.
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