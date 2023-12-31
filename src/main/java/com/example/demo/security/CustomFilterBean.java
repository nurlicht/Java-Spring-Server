package com.example.demo.security;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomFilterBean extends GenericFilterBean {

    private final String API_KEY_HEADER = "X-API-KEY";

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private final String apiKeyEncrypted;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fltr) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) req;
        final Optional<ApiAuthentication> auth = Optional.ofNullable(API_KEY_HEADER)
            .map(httpReq::getHeader)
            .filter(StringUtils::isNotBlank)
            .filter(s -> bCryptPasswordEncoder.matches(s, apiKeyEncrypted))
            .map(ApiAuthentication::new)
            ;
        if (auth.isPresent()) {
            SecurityContextHolder.getContext().setAuthentication(auth.get());
        } else {
            ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        fltr.doFilter(req, res);
    }
    
}
