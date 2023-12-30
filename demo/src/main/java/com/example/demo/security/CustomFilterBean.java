package com.example.demo.security;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomFilterBean extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain fltr) throws IOException, ServletException {
        final HttpServletRequest httpReq = (HttpServletRequest) req;
        final Optional<ApiAuthentication> auth = Optional.of(ApiKeyProvider.getEntity())
            .filter(entry -> entry.getValue().equals(httpReq.getHeader(entry.getKey())))
            .map(Map.Entry::getValue)
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
