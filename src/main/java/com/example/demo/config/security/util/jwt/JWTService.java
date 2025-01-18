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
    public static final int ACCESS_TIME = 60 * 60 * 1000; // 1시간
    private static final String SECRET = "very_stronger_secret";
    public static final String ACCESS = "access_token";
    public static final String REFRESH = "refresh_token";
    public static final int UNLIMITED_TIME = Integer.MAX_VALUE;

    private static JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();

    public JWTBuilder createToken(String subject, int id){
        return new JWTBuilder(
                JWT.create()
                    .withSubject(subject)
                        .withClaim("id", id)
        );
    }

    public class JWTBuilder {
        private JWTCreator.Builder builder;

        private JWTBuilder(JWTCreator.Builder builder) {
            this.builder = builder;
        }

        public JWTBuilder claim(String name, int value) {
            builder.withClaim(name, value);
            return this;
        }
        public JWTBuilder claim(String name, String value) {
            builder.withClaim(name, value);
            return this;
        }
        public JWTBuilder claim(String name, List<String> value) {
            builder.withClaim(name, value);
            return this;
        }
        public JWTBuilder expiresAt(int time){
            builder.withExpiresAt(new Date(System.currentTimeMillis() + time)); return this;
        }

        public String build() {
            try {
                return builder.sign(Algorithm.HMAC256(SECRET));
            }catch (Exception e) {
                e.printStackTrace();
                throw new TokenCreateFailException(e.getMessage());
            }
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
                    throw new ValidateFailException(key + "token:"+decodedJWT.getClaim(key).asString()+", input:"+validateMap.get(key));
            }
        }

        CustomAuthentication tokenAuthentic = new CustomAuthentication(
                decodedJWT.getClaim("id").asInt(),
                decodedJWT.getClaim("email").asString(),
                null,
                decodedJWT.getClaim("authorities").asList(String.class));

        if(decodedJWT.getClaim("level") != null)
            tokenAuthentic.setRefreshLevel(decodedJWT.getClaim("level").asInt());
            // if refresh token has level

        return tokenAuthentic;
    }
}