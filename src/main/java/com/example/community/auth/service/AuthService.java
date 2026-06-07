package com.example.community.auth.service;

import com.example.community.auth.dto.LoginRequest;
import com.example.community.auth.dto.LoginResponse;
import com.example.community.auth.dto.SignupRequest;
import com.example.community.auth.security.JwtTokenProvider;
import com.example.community.member.domain.Member;
import com.example.community.member.domain.Role;
import com.example.community.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    // 비밀번호를 암호화하고 검증할 때 사용
    // SecurityConfig에서 BCryptPasswordEncoder를 Bean으로 등록했기 때문에 여기서 주입받을 수 있다.
    private final PasswordEncoder passwordEncoder;

    // 로그인 성공 후 JWT를 만들기 위해 사용
    private final JwtTokenProvider jwtTokenProvider;

    public void signup(SignupRequest request) {

        // 같은 이메일로 가입한 회원이 있으면 회원가입을 막는다.
        // 이메일은 로그인 ID처럼 사용되기 때문에 중복되면 안 된다.
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호는 절대 평문으로 저장하면 안 된다.
        // passwordEncoder.encode()를 사용하면 원래 비밀번호를 알아보기 어려운 해시값으로 바뀐다.
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 회원가입 요청 DTO를 실제 회원 도메인 객체로 바꾼다.
        // 기본 권한은 일반 사용자 USER로 저장한다.
        Member member = new Member(
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                Role.USER
        );

        // 회원 정보를 저장소에 저장한다.
        memberRepository.save(member);
    }

    public LoginResponse login(LoginRequest request) {

        // 이메일로 회원을 찾는다.
        // 회원이 없으면 이메일이 틀렸는지 비밀번호가 틀렸는지 구체적으로 알려주지 않는다.
        // 보안상 둘 다 같은 메시지로 처리하는 편이 낫다.
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 틀렸습니다."));

        // 사용자가 입력한 비밀번호와 저장된 암호화 비밀번호가 일치하는지 확인한다.
        // BCrypt는 매번 다른 해시가 만들어질 수 있으므로 equals() 비교를 하면 안 된다.
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 틀렸습니다.");
        }

        // 로그인에 성공하면 JWT를 생성한다.
        // 서버 세션에 저장하지 않고, 토큰을 클라이언트에게 반환한다.
        String accessToken = jwtTokenProvider.createToken(member);

        return new LoginResponse(accessToken);
    }

    public void logout() {

    }
}