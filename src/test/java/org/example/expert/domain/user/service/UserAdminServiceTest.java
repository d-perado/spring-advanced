package org.example.expert.domain.user.service;

import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    void 유저권한변경_성공() {
        //given
        long userId = 1L;
        User user = new User("email@email.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", userId);
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        //when
        userAdminService.changeUserRole(userId, request);
        //then
        assertThat(user.getUserRole()).isEqualTo(UserRole.ADMIN);
    }
}
