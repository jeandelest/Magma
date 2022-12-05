package fr.insee.rmes.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@ConditionalOnExpression("'PROD'.equals('${fr.insee.rmes.magma.envir}')")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    @Value("${fr.insee.rmes.magma.cors.allowedOrigin}")
    private Optional<String> allowedOrigin;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    protected String issuerUri;

    @Value("${spring.security.oauth2.resourceserver.jwt.audience}")
    protected String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.sub-replace-by}")
    protected String subReplaceBy;

    @Value("${spring.security.enabled:true}")
    protected boolean enabledSecurity;

    @Value("${spring.security.oauth2.resourceserver.jwt.roles.resource-name:}")
    protected String resourceName;

    @Value("${spring.security.oauth2.resourceserver.jwt.roles.for-realm:true}")
    protected boolean roleForRealm;

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = (NimbusJwtDecoder) JwtDecoders.fromOidcIssuerLocation(issuerUri);
       // OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer);
        jwtDecoder.setJwtValidator(withAudience);
        jwtDecoder.setClaimSetConverter(new ReplaceSubClaimAdapter());
        return jwtDecoder;
    }

    // Customization to get Keycloak Role and get preffered_username as principal
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
        return jwtAuthenticationConverter;
    }

    Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return new Converter<Jwt, Collection<GrantedAuthority>>() {
            @Override
            @SuppressWarnings({"unchecked"})
            public Collection<GrantedAuthority> convert(Jwt source) {
                return ((Map<String, List<String>>) source.getClaim("realm_access")).get("roles").stream()
                        .map(s -> new GrantedAuthority() {
                            @Override
                            public String getAuthority() {
                                return "ROLE_" + s;
                            }

                            @Override
                            public String toString() {
                                return getAuthority();
                            }
                        }).collect(Collectors.toList());
            }
        };
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // disable csrf because of API mode
        http.csrf().disable();
        // allow jwt bearer authentication
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> {
            jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter());
        }));
        // configuration pour Swagger
        http.authorizeRequests(
                authz -> authz.antMatchers(HttpMethod.OPTIONS).permitAll()
                        .antMatchers("/", "/swagger-ui-magma.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .antMatchers("/operations/series").authenticated()
                        .antMatchers("/operations/serie/{id}/operations").authenticated()
                        .antMatchers("/operations/serie/{id}/").authenticated()
                        .antMatchers("/operations/operation/{id}").authenticated()
                        .antMatchers("/listesCodes").authenticated()
                        .antMatchers("/listeCode/{id}").authenticated()
                        .antMatchers("/structures").authenticated()
                        .antMatchers("/structure/{id}").authenticated()
                        .antMatchers("/composants").authenticated()
                        .antMatchers("/composant/{id}").authenticated()
                        .antMatchers("/concepts").authenticated()
                        .antMatchers("/concept/{id}").authenticated()
                        .antMatchers("/datasets/list").authenticated()
                        .antMatchers("/datasets/{id}").authenticated()
                        .anyRequest().authenticated());
        // autorisation d'afficher des frames dans l'appli pour afficher la console h2
        // (risque de clickjacking)
        http.headers().frameOptions().sameOrigin();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        logger.info("Allowed origins : {}", allowedOrigin);
        configuration.setAllowedOrigins(List.of(allowedOrigin.get()));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new
                UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    class ReplaceSubClaimAdapter implements Converter<Map<String, Object>, Map<String, Object>> {

        private final MappedJwtClaimSetConverter delegate = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());

        @Override
        public Map<String, Object> convert(Map<String, Object> claims) {
            Map<String, Object> convertedClaims = this.delegate.convert(claims);
            String substitueValue = (String) convertedClaims.get(subReplaceBy);
            convertedClaims.put("sub", substitueValue);
            return convertedClaims;
        }
    }
}

