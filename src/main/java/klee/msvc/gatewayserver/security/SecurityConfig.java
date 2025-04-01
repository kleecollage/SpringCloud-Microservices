package klee.msvc.gatewayserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
// @EnableWebFluxSecurity // REACT WEBFLUX
public class SecurityConfig {
    @Bean
    // SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception { // REACT WEBFLUX IMPL
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // return http.authorizeExchange(authorize -> { // REACT WEBFLUX
        return http.authorizeHttpRequests(authorize -> {
            // authorize.pathMatchers("/authorized", "/logout").permitAll() // REACT WEBFLUX
            authorize.requestMatchers("/authorized", "/logout").permitAll()
                    // .pathMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users") // REACT WEBFLUX
                    .requestMatchers(HttpMethod.GET, "/api/items", "/api/products", "/api/users")
                    .permitAll()

                    /* TO WORK WITH SCOPES READ/WRITE
                    .pathMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "api/users/{id}")
                    .hasAnyAuthority("SCOPE_write", "SCOPE_read")*/

                    // TO WORK WITH ROLES
                    .requestMatchers(HttpMethod.GET, "/api/items/{id}", "/api/products/{id}", "api/users/{id}")
                    .hasAnyRole("ADMIN", "USER")

                    .requestMatchers("/api/items/**", "/api/products/**", "/api/users/**")
                    .hasRole("ADMIN")
                    /*.pathMatchers(HttpMethod.POST, "/api/items", "/api/products", "/api/users").hasRole("ADMIN")
                    .pathMatchers(HttpMethod.PUT, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")
                    .pathMatchers(HttpMethod.DELETE, "/api/items/{id}", "/api/products/{id}", "/api/users/{id}").hasRole("ADMIN")*/
                    // .anyExchange().authenticated(); // REACT WEBFLUX
                     .anyRequest().authenticated();
        // }).cors(ServerHttpSecurity.CorsSpec::disable) // REACT WEBFLUX
        }).cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // DISABLE SESSIONS
                // .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // REACT WEBFLUX
                // .oauth2Login(withDefaults()) // REACT WEBFLUX
                .oauth2Login(login -> login.loginPage("/oauth2/authorization/client-app"))
                .oauth2Client(withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        // jwt -> jwt.jwtAuthenticationConverter(new Converter<Jwt, Mono<AbstractAuthenticationToken>>() { // REACT WEBFLUX
                        jwt -> jwt.jwtAuthenticationConverter(new Converter<Jwt, AbstractAuthenticationToken>() {
                            @Override
                            // public Mono<AbstractAuthenticationToken> convert(Jwt source) { // REACT WEBFLUX
                             public AbstractAuthenticationToken convert(Jwt source) {
                                Collection<String> roles = source.getClaimAsStringList("roles");
                                Collection<GrantedAuthority> authorities = roles.stream()
                                        .map(SimpleGrantedAuthority::new)
                                        .collect(Collectors.toList());
                                // return Mono.just(new JwtAuthenticationToken(source, authorities)); // REACT WEBFLUX
                                return new JwtAuthenticationToken(source, authorities);
                            }
                        })))
                .build();
    }
}


/* #########################   SECURITY CONFIG FOR  SPRING MVC (SERVLET)   ######################### */
//    @Configuration
//    public class SecurityConfig {
//
//        @Bean
//        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//            return http.authorizeHttpRequests((authz) -> {
//                        authz
//                                .requestMatchers("/authorized", "/logout").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/products", "/api/items", "/api/users").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/products/{id}", "/api/items/{id}", "/api/users/{id}").hasAnyRole("ADMIN", "USER")
//                                .requestMatchers("/api/products/**", "/api/items/**", "/api/users/**").hasRole("ADMIN")
//                                .anyRequest().authenticated();
//                    })
//                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                    .csrf(csrf -> csrf.disable())
//                    .oauth2Login(login -> login.loginPage("/oauth2/authorization/client-app"))
//                    .oauth2Client(withDefaults())
//                    .oauth2ResourceServer(withDefaults())
//                    .build();
//        }
//
//    }
