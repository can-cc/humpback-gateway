package com.fwchen.humpback.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.DefaultServerRedirectStrategy;
import org.springframework.security.web.server.ServerRedirectStrategy;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class MyServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {
    private ServerRedirectStrategy redirectStrategy = new DefaultServerRedirectStrategy();

    @Value("${application.frontend_url}")
    private String DEFAULT_LOGIN_SUCCESS_URL;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        URI url = URI.create(DEFAULT_LOGIN_SUCCESS_URL);
        return this.redirectStrategy
                .sendRedirect(webFilterExchange.getExchange(), url);
    }
}
