package com.github.seungjae97.alyak.alyakapiserver.domain.auth.service;

import com.github.seungjae97.alyak.alyakapiserver.domain.auth.JwtProperties;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.JwtTokenProvider;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.LoginRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Request.SignupRequest;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.LoginResponse;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.Role;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.User;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRole;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.entity.UserRoleId;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.RoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.user.repository.UserRoleRepository;
import com.github.seungjae97.alyak.alyakapiserver.domain.auth.dto.Response.TokenResponse;
import com.github.seungjae97.alyak.alyakapiserver.global.Redis.Util.RedisUtil;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessError;
import com.github.seungjae97.alyak.alyakapiserver.global.common.exception.BusinessException;
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
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .filter(u -> passwordEncoder.matches(loginRequest.getPassword(), u.getPassword()))
                .orElseThrow(() -> new BusinessException(BusinessError.INVALID_LOGIN));

        String token = jwtTokenProvider.generateToken(user);

        return new LoginResponse(
                token,
                jwtProperties.getExpirationTime(),
                user.getId()
        );
    }

    @Override
    @Transactional
    public void signup(SignupRequest signupRequest) {

        String email = signupRequest.getEmail();
        
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(BusinessError.EMAIL_ALREADY_EXISTS);
        }
        
        boolean isVerified = redisUtil.existData("verified:" + email);

        if (!isVerified) {
            boolean hasAuthCode = redisUtil.existData(email);

            if (hasAuthCode) {
                throw new BusinessException(BusinessError.EMAIL_NOT_VERIFIED);
            } else {
                throw new BusinessException(BusinessError.EMAIL_VERIFICATION_EXPIRED);
            }
        }

        User user = User.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .build();

        user = userRepository.save(user);

        Role defaultRole = roleRepository.findById(2)
                .orElseThrow(() -> new IllegalArgumentException("Default role not found"));

        UserRoleId userRoleId = new UserRoleId(user.getId(), defaultRole.getId());

        UserRole userRole = UserRole.builder()
                .id(userRoleId)
                .user(user)
                .role(defaultRole)
                .build();

        userRoleRepository.save(userRole);
    }

    @Override
    public void logout(String token) {
        // JWT는 stateless이므로 서버에서 토큰을 무효화할 수 없음
        // 클라이언트에서 토큰을 삭제하도록 안내
    }

    @Override
    public TokenResponse reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
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
