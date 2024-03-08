package fr.insee.rmes.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@ConditionalOnProperty(name = "fr.insee.rmes.magma.envir", havingValue = "PROD")
public class SecurityConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfiguration.class);
    @Value("${fr.insee.rmes.magma.cors.allowedOrigin}")
    private Optional<String> allowedOrigin;

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

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http)  throws Exception {
        http.sessionManagement(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(withDefaults()))
                .cors(withDefaults())
                // disable csrf because of API mode
                .csrf(AbstractHttpConfigurer::disable)
    // configuration pour Swagger
                .authorizeHttpRequests(
                        authorizeHttpRequest -> authorizeHttpRequest
                                .requestMatchers(HttpMethod.OPTIONS).permitAll()
                                .requestMatchers("/", "/swagger-ui-magma.html","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/operations/series").authenticated()
                                .requestMatchers("/operations/serie/{id}/operations").authenticated()
                                .requestMatchers("/operations/serie/{id}/").authenticated()
                                .requestMatchers("/operations/operation/{id}").authenticated()
                                .requestMatchers("/listesCodes").authenticated()
                                .requestMatchers("/listeCode/{id}").authenticated()
                                .requestMatchers("/structures").authenticated()
                                .requestMatchers("/structure/{id}").authenticated()
                                .requestMatchers("/composants").authenticated()
                                .requestMatchers("/composant/{id}").authenticated()
                                .requestMatchers("/concepts").authenticated()
                                .requestMatchers("/concept/{id}").authenticated()
                                .requestMatchers("/datasets/list").authenticated()
                                .requestMatchers("/dataset/{id}").authenticated()
                                .anyRequest().authenticated());
        // autorisation d'afficher des frames dans l'appli pour afficher la console h2
        // (risque de clickjacking)
        http.headers().frameOptions().sameOrigin();
        return http.build();
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
}

