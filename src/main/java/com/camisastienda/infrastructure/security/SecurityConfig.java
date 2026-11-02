package com.camisastienda.infrastructure.security;

import com.camisastienda.application.port.out.TokenProviderPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de seguridad stateless con JWT.
 * Reglas:
 *  - GET /api/productos/**        → público
 *  - POST/PUT/DELETE /api/productos/** → solo ADMIN
 *  - /api/carrito/** y /api/ordenes/** → solo CLIENTE
 *  - /api/auth/**                 → público (registro y login)
 */
@Configuration
public class SecurityConfig {

    private final TokenProviderPort tokenProvider;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(TokenProviderPort tokenProvider,
                          JwtAuthenticationEntryPoint entryPoint,
                          JwtAccessDeniedHandler accessDeniedHandler) {
        this.tokenProvider = tokenProvider;
        this.entryPoint = entryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        // Infraestructura y documentación
                        .requestMatchers("/actuator/health/**", "/v3/api-docs/**",
                                "/swagger-ui/**", "/swagger-ui.html", "/error").permitAll()
                        // Autenticación pública
                        .requestMatchers("/api/auth/**").permitAll()
                        // Productos: lectura pública, escritura solo ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/productos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                        // Carrito y órdenes solo CLIENTE
                        .requestMatchers("/api/carrito/**").hasRole("CLIENTE")
                        .requestMatchers("/api/ordenes/**").hasRole("CLIENTE")
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated())
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
