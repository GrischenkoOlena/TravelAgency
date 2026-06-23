package com.epam.finaltask.config;

import com.epam.finaltask.token.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/index.html", "/error", "/cabinet").permitAll();
                    auth.requestMatchers( "/static/**", "/css/**", "/js/**").permitAll();
                    auth.requestMatchers("/auth/login", "/auth/register").permitAll();

                    auth.anyRequest().authenticated();

                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutUrl("/auth/perform_logout")
                        .logoutSuccessUrl("/?logout=true")
                        .deleteCookies("JWT_TOKEN")
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                        .permitAll()
                )
                .build();
    }
}
