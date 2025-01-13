package com.example.demo.utils.jwt;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.utils.jwt.exception.ValidateFailException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JWTService {
    private static final int ACCESS_TIME = 60 * 60 * 1000; // 1시간
    private static final String SECRET = "very_stronger_secret";
    public static final String ACCESS = "access_token";
    public static final String REFRESH = "refresh_token";

    private JWTVerifier jwtVerifier;

    public JWTService(){
        jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
    }

    public String createAccessToken(String email, List<String> authorities, String inputIp, String agent) {
        return JWT.create()
                .withSubject(ACCESS)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TIME))
                .withClaim("email", email)
                .withClaim("authorities", authorities)
                .withClaim("ip", inputIp)
                .withClaim("agent", agent)
                .sign(Algorithm.HMAC256(SECRET));
    }

    public String createRefreshToken(String email, List<String> authorities) {
        return JWT.create()
                .withSubject(ACCESS)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withClaim("email", email)
                .withClaim("authorities", authorities)
                .sign(Algorithm.HMAC256(SECRET));
    }

    public Authentication getAuthentication(String token, Map<String, String> validateMap) {
        DecodedJWT decodedJWT = jwtVerifier.verify(token);

        if(validateMap!=null && !validateMap.isEmpty()) {
            for (String key : validateMap.keySet()) {
                if (!decodedJWT.getClaim(key).asString().equals(validateMap.get(key)))
                    throw new ValidateFailException(key);
            }
        }

        return new CustomAuthentication(decodedJWT.getClaim("email").asString(), null, decodedJWT.getClaim("authorities").asList(String.class));
    }

    public static void validateSame(DecodedJWT decodedJWT,String key, String expected) {
        if(!decodedJWT.getClaim(key).equals(expected))
            throw new ValidateFailException(key);
    }
}