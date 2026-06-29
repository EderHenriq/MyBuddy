package com.Mybuddy.Myb.Security;

import lombok.*;

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
