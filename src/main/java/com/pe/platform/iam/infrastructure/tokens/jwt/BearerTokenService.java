package com.pe.platform.iam.infrastructure.tokens.jwt;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import com.pe.platform.iam.application.internal.outboundservices.tokens.TokenService;

public interface BearerTokenService extends TokenService {

    String getBearerTokenFrom(HttpServletRequest request);
    String generateToken(Authentication authentication);

}