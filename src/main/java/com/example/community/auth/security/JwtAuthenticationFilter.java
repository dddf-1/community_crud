package com.example.community.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // CORS 사전 요청은 JWT 검사하지 않고 통과

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (path.startsWith("/health")
                || path.startsWith("/api/auth/signup")
                || path.startsWith("/api/auth/login")
                || path.startsWith("/v1/auth/signup")
                || path.startsWith("/v1/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 Authorization 값을 꺼낸다.
        // JWT는 보통 Authorization: Bearer 토큰값 형태로 전달한다.
        String authorizationHeader = request.getHeader("Authorization");

        // Authorization 헤더가 없거나 Bearer 형식이 아니면
        // 인증 정보가 없는 요청으로 보고 다음 필터로 넘긴다.
        // 이 요청이 허용되는지는 SecurityConfig의 권한 설정이 최종 판단한다.
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 뒤에 있는 실제 JWT 문자열만 잘라낸다.
        String token = authorizationHeader.substring(7);

        // 토큰이 정상인지 검증한다.
        // 서명이 틀렸거나 만료되었으면 인증 객체를 만들지 않는다.
        if (!jwtTokenProvider.validateToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰에서 로그인 사용자 정보를 꺼낸다.
        CustomUserPrincipal principal = jwtTokenProvider.getPrincipal(token);

        // Spring Security 권한 형식은 보통 ROLE_USER, ROLE_ADMIN 형태를 사용한다.
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + principal.getRole().name())
        );

        // Spring Security가 이해할 수 있는 인증 객체를 만든다.
        // principal: 로그인 사용자 정보
        // credentials: JWT 인증에서는 비밀번호를 다시 저장하지 않으므로 null
        // authorities: 사용자 권한
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        authorities
                );

        // 현재 요청을 처리하는 동안 사용할 인증 정보를 SecurityContextHolder에 저장한다.
        // Controller에서는 @AuthenticationPrincipal로 principal을 꺼낼 수 있다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 다음 필터 또는 Controller로 요청을 넘긴다.
        filterChain.doFilter(request, response);
    }
}
