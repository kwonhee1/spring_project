package com.example.demo.config.security.filters;


import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.util.blacklist.BlackListService;
import com.example.demo.config.security.util.jwt.JWTService;
import com.example.demo.config.security.util.jwt.exception.ValidateFailException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomAccessTokenFilter extends CustomTokenFilter {
    public CustomAccessTokenFilter() {
        super(JWTService.ACCESS,
                new CustomRequestMatchers(CustomRequestMatchers.TokenPattern, CustomRequestMatchers.ALL_METHOD),
                new JWTService()
        );
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        request.setCharacterEncoding("UTF-8");

        String access_token = getToken(request, JWTService.ACCESS);

        HashMap<String, String> jwtValidateMap = new HashMap<>();
        jwtValidateMap.put("ip", getIpFromRequest(request));
        jwtValidateMap.put("agent", getAgentFromRequest(request));

        Authentication authentication = jwtService.getAuthentication(access_token, jwtValidateMap);
        return getAuthenticationManager().authenticate(authentication);
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
            BlackListService.addBlackList(getIpFromRequest(request));

            System.out.println("CustomAccessTokenFilter :: failureHandler :: access token validate fail " + e.getMessage() + " target ip : " + getIpFromRequest(request) +" is added to blackList");
        }
    }
}
