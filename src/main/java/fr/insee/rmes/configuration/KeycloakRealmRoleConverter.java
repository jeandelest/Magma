package fr.insee.rmes.configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private String resourceName;
    private boolean roleForRealm = true;

    public static final String REALM_ACCESS = "realm_access";
    public static final String RESOURCE_ACCESS = "resource_access";
    public static final String ROLES = "roles";
    public static final String ROLE_PREFIX = "ROLE_";

    public KeycloakRealmRoleConverter() {
    }

    public KeycloakRealmRoleConverter(boolean roleForRealm, String resourceName) {
        this.roleForRealm = roleForRealm;
        this.resourceName = resourceName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<GrantedAuthority> convert(final Jwt jwt) {
        Map<String, Object> access;
        if (!roleForRealm && resourceName != null) {
            access = (Map<String, Object>) jwt.getClaims().get(RESOURCE_ACCESS);
            if (access.containsKey(resourceName)) {
                access = (Map<String, Object>) access.get(resourceName);
            }
        } else {
            access = (Map<String, Object>) jwt.getClaims().get(REALM_ACCESS);
        }
        return ((List<String>) access.get(ROLES)).stream()
                .map(roleName -> ROLE_PREFIX + roleName.toUpperCase())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
