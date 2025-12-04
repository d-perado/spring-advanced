package org.example.expert.domain.user.service;

import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void 유저_조회() {
        //given
        long userId = 1L;
        User user = new User("email","password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        //when
        UserResponse result = userService.getUser(userId);

        //then
        assertThat(result.getId()).isEqualTo(userId);
        assertThat(result.getEmail()).isEqualTo("email");

    }

    @Test
    void 비밀번호_변경_성공() {
        // given
        long userId = 1L;
        UserChangePasswordRequest request =
                new UserChangePasswordRequest("oldPassword", "newPassword");

        User user = new User("email", "encodedOldPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        given(passwordEncoder.matches("oldPassword", "encodedOldPassword"))
                .willReturn(true);
        given(passwordEncoder.matches("newPassword", "encodedOldPassword"))
                .willReturn(false);
        given(passwordEncoder.encode("newPassword"))
                .willReturn("encodedNewPassword");

        // when
        userService.changePassword(userId, request);

        // then
        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    void 비밀번호_변경_실패_변경비밀번호와_같음() {
        // given
        long userId = 1L;
        UserChangePasswordRequest request =
                new UserChangePasswordRequest("oldPassword", "newPassword");

        User user = new User("email", "encodedOldPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        given(passwordEncoder.matches("newPassword", "encodedOldPassword"))
                .willReturn(true);
        // when
        // then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
    }

    @Test
    void 비밀번호_변경_실패_비밀번호_검증실패() {
        // given
        long userId = 1L;
        UserChangePasswordRequest request =
                new UserChangePasswordRequest("oldPassword", "newPassword");

        User user = new User("email", "encodedOldPassword", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        given(passwordEncoder.matches("newPassword", "encodedOldPassword"))
                .willReturn(false);
        given(passwordEncoder.matches("oldPassword", "encodedOldPassword"))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("잘못된 비밀번호입니다.");
    }

}
