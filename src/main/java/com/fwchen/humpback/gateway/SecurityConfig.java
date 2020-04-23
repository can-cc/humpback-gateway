package com.fwchen.humpback.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Value("${application.frontend_url}")
    private String frontendUrl;

    private final MyServerAuthenticationSuccessHandler myServerAuthenticationSuccessHandler;

    public SecurityConfig(MyServerAuthenticationSuccessHandler myServerAuthenticationSuccessHandler) {
        this.myServerAuthenticationSuccessHandler = myServerAuthenticationSuccessHandler;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList(frontendUrl));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        http.cors();

        http.oauth2Login().authenticationSuccessHandler(myServerAuthenticationSuccessHandler);

        http.logout(logout -> logout.logoutSuccessHandler(
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)));
        http.logout().logoutUrl("/auth/logout");

        http.authorizeExchange().anyExchange().authenticated();

        // http.exceptionHandling().accessDeniedHandler(myAccessDeniedHandlerWebFlux).authenticationEntryPoint((exchange, exception) -> Mono.error(exception));

        http.headers().frameOptions().disable().xssProtection().disable();
        http.csrf().disable();
        http.httpBasic().disable();
        http.formLogin().disable();
        return http.build();
    }
}