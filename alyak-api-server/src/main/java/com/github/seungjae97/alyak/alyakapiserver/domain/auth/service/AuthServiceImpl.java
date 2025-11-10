package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.JwtProperties;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.JwtTokenProvider;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.LoginRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.SignupRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.LoginResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtTokenProvider.generateToken(user);
        
        return new LoginResponse(
            token,
            jwtProperties.getPrefix().trim(),
            jwtProperties.getExpirationTime(),
            user.getId(),
            user.getRole()
        );
    }

    @Override
    public void signup(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .build();

        userRepository.save(user);
    }

    @Override
    public void logout(String token) {
        // JWT는 stateless이므로 서버에서 토큰을 무효화할 수 없음
        // 클라이언트에서 토큰을 삭제하도록 안내
    }

    @Override
    public TokenResponse reissue(String refreshToken) {
        if(!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId).orElseThrow();

        String newAccess = jwtTokenProvider.generateToken(user);
        String newRefresh = jwtTokenProvider.generateRefreshToken(user);

        return TokenResponse.builder()
                .accessToken(newAccess)
                .refreshToken(newRefresh)
                .email(user.getEmail())
                .build();
    }

    @Override
    public TokenResponse tempLogin() {
        User user = userRepository.findById(1L).orElseThrow();

        String accessToken = jwtTokenProvider.generateToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        String email = user.getEmail();

        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).email(email).build();
    }
}
