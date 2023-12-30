package com.example.demo.security;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Abstraction for package-level access to the API-key
 *   - Possibility of encryption, reading from properties, arbitrary sources ...
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ApiKeyProvider {

    @Getter(value = AccessLevel.PACKAGE)
    private static final Map.Entry<String, String> entity = Map.of(
        "X-API-KEY",
        "X-API-VALUE"
    ).entrySet().iterator().next();
}
