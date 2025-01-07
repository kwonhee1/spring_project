package com.example.demo.utils;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.config.security.authentication.CustomAuthentication;

import com.example.demo.model.Member;
import com.example.demo.role.Permission;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class JWTService {
    private static final int ACCESS_TIME = 60 * 60 * 1000; // 1시간
    private static final String SECRET = "very_stronger_secret";
    public static final String ACCESS = "access_token";

    private JWTVerifier jwtVerifier;
    private DecodedJWT decodedJWT;

    public JWTService(){
        jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
    }

    public JWTService(DecodedJWT decodedJWT){
        this.decodedJWT = decodedJWT;
    }

    public String createAccessToken(String email, Collection<? extends GrantedAuthority> authorities, String inputIp, String agent) {
        return JWT.create()
                .withSubject(ACCESS)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TIME))
                .withClaim("email", email)
                .withClaim("authorities", (List<Permission>)authorities)
                .withClaim("ip", inputIp)
                .withClaim("agent", agent)
                .sign(Algorithm.HMAC256(SECRET));
    }

    public JWTService validateAccessToken(String token, String ip, String agent){
        try{
            DecodedJWT decodedJWT = jwtVerifier.verify(token);

            // ip and agent 확인
            if(!decodedJWT.getClaim("ip").equals(ip)|| !decodedJWT.getClaim("agent").equals(agent)){
                return null;
            }

            return new JWTService(decodedJWT);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public UsernamePasswordAuthenticationToken getAuthentication(){
        return new UsernamePasswordAuthenticationToken(decodedJWT.getClaim("email"), null, (Collection<? extends GrantedAuthority>) decodedJWT.getClaim("authorities"));
    }
}
