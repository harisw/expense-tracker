package com.harisw.springexpensetracker.application.auth.port;

import com.harisw.springexpensetracker.domain.auth.User;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;
import java.util.UUID;

public interface TokenService {
    String generateAccessToken(User user) throws JOSEException;

    String generateRefreshToken(User user) throws JOSEException;

    UUID extractUserPublicId(String token);

    boolean isValid(String token) throws ParseException;
}
