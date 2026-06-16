package com.example.community.global.config;

import com.example.community.auth.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰을 검사하는 필터
    // Spring Security 필터 체인에 직접 등록해서 Controller 실행 전에 먼저 동작하게 한다.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // JWT 방식에서는 서버 세션을 사용하지 않는다.
                // 그래서 CSRF 공격 대상이 되는 세션 기반 인증을 끈다.
                .csrf(AbstractHttpConfigurer::disable)

                // 기본 로그인 폼을 사용하지 않는다.
                // /api/auth/login에서 JSON으로 로그인하고 JWT를 발급함.
                .formLogin(AbstractHttpConfigurer::disable)

                // 브라우저 기본 팝업 로그인 방식을 사용하지 않는다.
                .httpBasic(AbstractHttpConfigurer::disable)

                // JWT는 서버가 로그인 상태를 세션에 저장하지 않는다.
                // 요청마다 토큰을 검증해서 사용자를 확인하기 때문에 STATELESS로 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 어떤 API는 누구나 접근 가능하고,
                // 어떤 API는 로그인한 사용자만 접근 가능하게 나눈다.
                .authorizeHttpRequests(auth -> auth

                                // CORS preflight 요청 허용
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                // 로그인 전에도 가능한 API만 허용
                                .requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
                                // 게시글 조회는 공개
                                .requestMatchers(HttpMethod.GET, "/api/posts", "/api/posts/*").permitAll()
                                // 업로드된 이미지 조회 공개
                                .requestMatchers(HttpMethod.GET, "/uploads/**").permitAll()
                                // 나머지는 인증 필요
                                .anyRequest().authenticated()
                )

                // UsernamePasswordAuthenticationFilter 앞에 JWT 필터를 둔다.
                // --> Spring Security가 기본 인증을 처리하기 전에 JWT 인증을 먼저 확인할 수 있음
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호를 평문으로 저장하지 않고 BCrypt 해시로 저장하기 위해 사용
        // 회원가입 때 encode(), 로그인 때 matches()를 사용
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://54.180.147.149:3000",
                "http://localhost:8080",  // 로컬 개발용 프론트
                "http://127.0.0.1:8080"  // 로컬 개발용 프론트 (IP)
        ));

        configuration.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}