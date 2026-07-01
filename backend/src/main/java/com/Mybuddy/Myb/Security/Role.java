package com.Mybuddy.Myb.Security;

import lombok.*;

/**
 * Representa uma role atribuída a um usuário, embutida no documento do Usuario no MongoDB.
 */
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @NonNull
    @EqualsAndHashCode.Include
    private ERole name;
}
