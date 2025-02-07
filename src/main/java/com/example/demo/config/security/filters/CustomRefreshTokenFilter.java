package com.example.demo.config.security.filters;

import com.example.demo.config.security.CustomRequestMatchers;
import com.example.demo.config.security.authentication.AuthenticationFailException;
import com.example.demo.config.security.authentication.CustomAuthentication;
import com.example.demo.config.security.util.jwt.model.AccessToken;
import com.example.demo.config.security.util.jwt.model.RefreshToken;
import com.example.demo.model.Member;
import com.example.demo.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;

//refresh token 검증
//				성공 : access token 발급 + security context에 authentication저장
//				실패 : refresh token 지우고 로그인 페이지로 redirect
public class CustomRefreshTokenFilter extends CustomTokenFilter{
    protected Authentication authentication;
    private MemberService memberService;

    public CustomRefreshTokenFilter(MemberService memberService) {
        super(
                RefreshToken.REFRESH,
                new CustomRequestMatchers(CustomRequestMatchers.TokenPattern, CustomRequestMatchers.ALL_METHOD));

        // data base에서 refresh token의 level 값을 확인함
        this.memberService = memberService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.isAuthenticated()){
            // authenticaion 객체가 이매 생성되었다면 그냥 다음으로 넘어감
            chain.doFilter(request,response);
            return;
        }

        super.doFilter(request,response, chain);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // authentication 없음 (access 없음) => refresh 얻기
        String refresh = (String)request.getAttribute(RefreshToken.REFRESH);

        if(refresh == null || refresh.isEmpty()){
            return null;
        }

        return getAuthenticationManager().authenticate(new RefreshToken().decode(refresh));
    }

    @Override
    protected void successHandler(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // refresh 존재 => access 발급 + security context에 authenticaion저장
        String token =
                new RefreshToken((CustomAuthentication) authentication).toString();
        response.addCookie(createCookie(AccessToken.ACCESS, token, AccessToken.ACCESS_TIME));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected void failureHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        // refresh 이상함 or 존재 안함 => refresh 만료 (redirect 걸지 않음 차후 security에 의해 차당 과정 진입

        // refresh  만료
        response.addCookie(createCookie(RefreshToken.REFRESH, "", 0));
        // access 도 만료
        response.addCookie(createCookie(AccessToken.ACCESS, "", 0));
    }

    @Override
    protected Authentication provider(Authentication authentication) {
        // db에서 authenticaion가져옴
        Member DBMember = memberService.getMemberByMemberId((int)authentication.getPrincipal());

        // db에 있는 level과 refresh token의 level을 비교하는 과정 필요 -> failure Handler 실행을 위한 exception 발생
        if(!memberService.checkRefreshLevel( (int) ((CustomAuthentication)authentication).getPrincipal(),
                ((CustomAuthentication) authentication).getRefreshLevel()))
            throw new AuthenticationFailException("by refresh level : CustomRefreshTokenFilter >> authenticationManager");

        // authentication에 필요한 정보 추가
        ((CustomAuthentication) authentication).setEmail(DBMember.getEmail());
        ((CustomAuthentication) authentication).setRoles(DBMember.getRoles());

        authentication.setAuthenticated(true);

        return authentication;
    }
}
