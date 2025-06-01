package com.eduardo.apisystem.config.security;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {
    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((session) -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests((requests) -> {
                    requests.requestMatchers(
                            "/api/system/auth/**",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/swagger-resources/**",
                            "/swagger-ui.html",
                            "/api/images/**"
                    ).permitAll();

                    requests.requestMatchers(HttpMethod.POST, "/api/system/usuarios").permitAll();
                    requests.requestMatchers(HttpMethod.GET, "/api/system/usuarios/verificar-conta").permitAll();

                    requests.requestMatchers(HttpMethod.PATCH, "/api/system/usuarios/{usuarioId}/perfil").hasRole("ADMINISTRADOR");
                    requests.requestMatchers(HttpMethod.GET, "/api/system/usuarios").hasRole("DEV");

                    requests.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        String hierarchy = "ADMINISTRADOR > DEV\n" +
                "DEV > USUARIO";

        return RoleHierarchyImpl.fromHierarchy(hierarchy);
    }
}