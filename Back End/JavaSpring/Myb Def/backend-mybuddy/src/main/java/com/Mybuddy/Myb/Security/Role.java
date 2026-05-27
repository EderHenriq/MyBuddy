package com.Mybuddy.Myb.Security;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

/**
 * Entidade Role adaptada para o MongoDB.
 * Define os perfis de acesso do sistema (ADMIN, ONG, ADOTANTE, PETSHOP).
 */
@Document(collection = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @NonNull
    private ERole name;
}