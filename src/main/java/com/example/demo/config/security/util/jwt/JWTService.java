package com.example.demo.config.security.util.jwt;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.config.security.util.jwt.exception.InvalidTokenException;
import com.example.demo.config.security.util.jwt.exception.TokenCreateFailException;
import com.example.demo.config.security.util.jwt.exception.ValidateFailException;
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
    public static final int UNLIMITED_TIME = Integer.MAX_VALUE;

    private static JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();

    public String createToken(String subject, Map<String, String> cliams, List<String> authorities) {
        try {
            JWTCreator.Builder builder = JWT.create()
                    .withSubject(subject)
                    .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TIME))
                    .withClaim("authorities", authorities);

            for (String key : cliams.keySet()) {
                builder.withClaim(key, cliams.get(key));
            }

            return builder.sign(Algorithm.HMAC256(SECRET));
        } catch (Exception e) {
            e.printStackTrace();
            throw new TokenCreateFailException(e.getMessage());
        }
    }

    public Authentication getAuthentication(String token, Map<String, String> validateMap) {
        DecodedJWT decodedJWT;
        try {
            decodedJWT = jwtVerifier.verify(token);
        }catch (Exception e){
            throw new InvalidTokenException();
        }

        if(validateMap!=null && !validateMap.isEmpty()) {
            for (String key : validateMap.keySet()) {
                if (!decodedJWT.getClaim(key).asString().equals(validateMap.get(key)))
                    throw new ValidateFailException(key);
            }
        }

        return new CustomAuthentication(decodedJWT.getClaim("email").asString(), null, decodedJWT.getClaim("authorities").asList(String.class));
    }

    public static void validateSame(DecodedJWT decodedJWT,String key, String expected) {
        if(key == null || expected == null)
            throw new ValidateFailException("can not input null for validate key or expected");

        if(!decodedJWT.getClaim(key).equals(expected))
            throw new ValidateFailException(key);
    }
}