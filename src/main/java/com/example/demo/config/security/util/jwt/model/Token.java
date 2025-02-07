package com.example.demo.config.security.util.jwt.model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.config.security.util.jwt.exception.InvalidTokenException;
import org.springframework.security.core.Authentication;

public abstract class Token <T> {
    private static final String SECRET = "very_stronger_secret";
    private static JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();

    protected T id; // userId : member(id) int auto_increment, pk

    public Token(){}
    public Token(T id){
        this.id = id;
    }
    public String toString(){
        return addCliams(JWT.create())
                .sign(Algorithm.HMAC256(SECRET));
    }
    protected abstract JWTCreator.Builder addCliams(JWTCreator.Builder builder);

    public Authentication decode(String str){
        DecodedJWT decodedJWT;
        try{
            decodedJWT = JWT.decode(str);
        } catch (JWTDecodeException e) {
            throw new InvalidTokenException();
        }

        validate(decodedJWT);

        return getAuthentication(decodedJWT);
    }
    protected abstract void validate(DecodedJWT decodedJWT);
    protected abstract Authentication getAuthentication(DecodedJWT decodedJWT);
}


// 1. 다른 클래스에서 AccessToken을 생성자를 통해 찍어 내고, accessToken.toSting()을 부르면 바로 Service를 통해 String화 되게 만드는 작업
// 2. String to AccessToken class 변환 jwtService에 함수 만들기