package com.Mybuddy.Myb.Security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    
    private final JwtGrantedAuthoritiesConverter  jwtGrantedAuthoritiesConverter = 
            new JwtGrantedAuthoritiesConverter();

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
            jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
            extractRealmRoles(jwt).stream()
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, jwt.getClaimAsString("preferred_username"));
    }

    private Collection<GrantedAuthority> extractRealmRoles(Jwt jwt){
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null) return List.of();

        List<String> roles = (List<String>) realmAccess.get("roles");
        if (roles == null) return List.of();

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
