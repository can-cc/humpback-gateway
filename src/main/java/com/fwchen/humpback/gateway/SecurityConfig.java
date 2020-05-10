package com.fwchen.humpback.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.DelegatingServerAuthenticationEntryPoint;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  private final MyServerAuthenticationSuccessHandler myServerAuthenticationSuccessHandler;
  @Value("${application.frontend_url}")
  private String frontendUrl;

  public SecurityConfig(MyServerAuthenticationSuccessHandler myServerAuthenticationSuccessHandler) {
    this.myServerAuthenticationSuccessHandler = myServerAuthenticationSuccessHandler;
  }

  //    @Bean
  //    CorsConfigurationSource corsConfigurationSource() {
  //        CorsConfiguration configuration = new CorsConfiguration();
  //        configuration.setAllowedOrigins(Collections.singletonList("*"));
  //        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTION"));
  //        configuration.setAllowCredentials(true);
  //        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
  //        source.registerCorsConfiguration("/**", configuration);
  //        return source;
  //    }

  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(
      ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository) {
    List<DelegatingServerAuthenticationEntryPoint.DelegateEntry> entryPoints = new ArrayList<>();
    entryPoints.add(
        new DelegatingServerAuthenticationEntryPoint.DelegateEntry(
            ServerWebExchangeMatchers.pathMatchers("/**"),
            new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));
    DelegatingServerAuthenticationEntryPoint nonAjaxLoginEntryPoint =
        new DelegatingServerAuthenticationEntryPoint(entryPoints);

    http.oauth2Login().authenticationSuccessHandler(myServerAuthenticationSuccessHandler);

    http.logout(
        logout ->
            logout.logoutSuccessHandler(
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository)));
    http.logout().logoutUrl("/auth/logout");

    http.exceptionHandling().authenticationEntryPoint(nonAjaxLoginEntryPoint);

    http.authorizeExchange().anyExchange().authenticated();

    http.csrf().disable();
    http.httpBasic().disable();
    http.formLogin().disable();
    return http.build();
  }
}
