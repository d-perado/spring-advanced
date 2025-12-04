package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAdminController.class)
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserAdminService userAdminService;

    @MockitoBean
    private AuthUserArgumentResolver argumentResolver;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
    }

    @Test
    void 유저권한_변경_성공() throws Exception {
        //given
        long userId = 1L;

        when(argumentResolver.supportsParameter(any())).thenReturn(true);
        when(argumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(authUser);

        doNothing().when(userAdminService)
                .changeUserRole(eq(userId), any(UserRoleChangeRequest.class));

        String jsonBody = objectMapper.writeValueAsString(new UserRoleChangeRequest("ADMIN"));

        //when
        mockMvc.perform(patch("/admin/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        //then
        verify(userAdminService).changeUserRole(eq(userId), any(UserRoleChangeRequest.class));
    }
}

