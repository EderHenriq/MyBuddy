package com.Mybuddy.Myb.Model;

import lombok.*;

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