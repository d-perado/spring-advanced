package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void 회원가입_실패() {
        // given
        SignupRequest request = new SignupRequest("email@email.com", "Password1", "USER");
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // when
        InvalidRequestException exception =
                assertThrows(InvalidRequestException.class, () -> authService.signup(request));

        // then
        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    void 회원가입_성공() {
        // given
        SignupRequest request = new SignupRequest("email@email.com", "Password1", "USER");

        when(userRepository.existsByEmail("email@email.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1")).thenReturn("encodedPw");

        User savedUser = new User("email@email.com", "encodedPw", UserRole.USER);
        ReflectionTestUtils.setField(savedUser, "id", 1L);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.createToken(1L, "email@email.com", UserRole.USER))
                .thenReturn("Bearer jwt-token");

        // when
        SignupResponse response = authService.signup(request);

        // then
        assertThat(response.getBearerToken()).isEqualTo("Bearer jwt-token");

        verify(userRepository).existsByEmail("email@email.com");
        verify(passwordEncoder).encode("Password1");
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).createToken(1L, "email@email.com", UserRole.USER);
    }

    @Test
    void 로그인_성공() {
        //given
        SigninRequest request = new SigninRequest("email@email.com", "Password1");

        User user = new User("email@email.com", "Password1", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        String bearerToken = "Bearer jwt-token";

        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(jwtUtil.createToken(any(), any(), any())).thenReturn(bearerToken);
        //when

        SigninResponse result = authService.signin(request);

        //then
        assertThat(result.getBearerToken()).isEqualTo("Bearer jwt-token");
    }

    @Test
    void 로그인_실패_비밀번호불일치() {
        //given
        SigninRequest request = new SigninRequest("email@email.com", "Password1");

        User user = new User("email@email.com", "Password1", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(user));
        //when
        AuthException exception =
                assertThrows(AuthException.class, () -> authService.signin(request));

        //then
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    void 로그인_실패_존재하지않는유저() {
        //given
        SigninRequest request = new SigninRequest("email@email.com", "Password1");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        //when
        InvalidRequestException exception =
                assertThrows(InvalidRequestException.class, () -> authService.signin(request));

        //then
        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }
}
