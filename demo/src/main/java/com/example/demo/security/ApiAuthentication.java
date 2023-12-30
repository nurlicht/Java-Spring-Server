package com.example.demo.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

class ApiAuthentication extends AbstractAuthenticationToken {

    private final String key;

    ApiAuthentication(final String key) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.key = key;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("Unimplemented method 'getCredentials'");
    }

    @Override
    public Object getPrincipal() {
        return this.key;
    }
}
