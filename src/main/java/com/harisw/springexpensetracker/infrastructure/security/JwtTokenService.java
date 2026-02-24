package com.harisw.springexpensetracker.infrastructure.security;

import com.harisw.springexpensetracker.application.auth.port.TokenService;
import com.harisw.springexpensetracker.domain.auth.User;
import com.harisw.springexpensetracker.domain.auth.exception.InvalidCredentialsException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenService implements TokenService {
    private final Integer accessTokenExpiry;
    private final Integer refreshTokenExpiry;
    private final MACSigner signer;
    private final MACVerifier verifier;

    public JwtTokenService(
            @Value("${spring.jwt.secret}") String secret,
            @Value("${spring.jwt.access-token-expiry}") Integer accessTokenExpiry,
            @Value("${spring.jwt.refresh-token-expiry}") Integer refreshTokenExpiry
    ) throws JOSEException {
        byte[] secretBytes = Base64.getDecoder().decode(secret);

        this.signer = new MACSigner(secretBytes);
        this.verifier = new MACVerifier(secretBytes);

        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;


    }

    @Override
    public String generateAccessToken(User user) throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.publicId().toString())
                .claim("email", user.email())
                .claim("role", user.role().name())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + accessTokenExpiry))
                .build();
        SignedJWT signed = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );
        signed.sign(signer);
        return signed.serialize();
    }

    @Override
    public String generateRefreshToken(User user) throws JOSEException {
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.publicId().toString())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + refreshTokenExpiry))
                .build();
        SignedJWT signed = new SignedJWT(
                new JWSHeader(JWSAlgorithm.HS256),
                claimsSet
        );
        signed.sign(signer);
        return signed.serialize();
    }

    @Override
    public UUID extractUserPublicId(String token) {
        try {
            SignedJWT signed = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signed.getJWTClaimsSet();
            return UUID.fromString(claimsSet.getSubject());
        } catch (ParseException e) {
            throw new InvalidCredentialsException();
        }
    }

    @Override
    public boolean isValid(String token) {
        try {
            SignedJWT signed = SignedJWT.parse(token);
            if (!signed.verify(verifier)) {
                return false;
            }
            JWTClaimsSet claimsSet = signed.getJWTClaimsSet();
            return claimsSet.getExpirationTime().after(new Date());
        } catch (ParseException | JOSEException e) {
            throw new InvalidCredentialsException();
        }
    }
}
