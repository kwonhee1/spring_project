package com.example.demo.config.security.filters;


import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.util.blacklist.BlackListService;
import com.example.demo.config.security.util.jwt.exception.ValidateFailException;
import com.example.demo.config.security.util.jwt.model.AccessToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

public class CustomAccessTokenFilter extends CustomTokenFilter {
    public CustomAccessTokenFilter() {
        super(AccessToken.ACCESS,
                new CustomRequestMatchers(CustomRequestMatchers.TokenPattern, CustomRequestMatchers.ALL_METHOD)
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        System.out.println("access filter");

        String access_token = (String)request.getAttribute(AccessToken.ACCESS);
        if(access_token == null || access_token.isEmpty()){
            return null;
        }
        return getAuthenticationManager().authenticate(new AccessToken().decode(access_token));
    }

    @Override
    protected void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 저장하는게 맞나?
        // context를 만드는게 맞나? 그냥 get하기만 하면끝인가?
    }

    @Override
    protected void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // access 만료 처리 안함 ->  refresh 에서 확인후 같이 처리

        // access 에서 무조건 처리해야하는 exception은? JSTService :: validate fail exception
        if(e instanceof ValidateFailException){
            // 다른 사람의 도용으로 간주 해당 ip backlist에 올리고 모든 token 만료 처리
            BlackListService.addBlackList((String)request.getAttribute("ip"));

            System.out.println("CustomAccessTokenFilter :: failureHandler :: access token validate fail " + e.getMessage() + " target ip : " + request.getAttribute("ip") +" is added to blackList");
        }
    }

    @Override
    protected Authentication provider(Authentication authentication) {
        // 아무 검증 절차도 진행하지 않음
        authentication.setAuthenticated(true);
        return authentication;
    }
}
