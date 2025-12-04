package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthUserArgumentResolver argumentResolver;

    private AuthUser authUser;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(){ authUser = new AuthUser(1L, "email@email.com", UserRole.USER);}

    @Test
    void 유저조회성공() throws Exception {
        //given
        long userId = 1L;

        when(argumentResolver.supportsParameter(any())).thenReturn(true);
        when(argumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(authUser);

        when(userService.getUser(userId))
                .thenReturn(new UserResponse(authUser.getId(),authUser.getEmail()));
        //when
        mockMvc.perform(get("/users/{userId}",userId))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(authUser.getId()))
                .andExpect(jsonPath("$.email").value(authUser.getEmail()));

    }

    @Test
    void 패스워드_변경_성공() throws Exception {
        //given
        UserChangePasswordRequest request = new UserChangePasswordRequest("oldPassword1","newPassword1");

        when(argumentResolver.supportsParameter(any())).thenReturn(true);
        when(argumentResolver.resolveArgument(any(),any(),any(),any()))
                .thenReturn(authUser);
        //when
        mockMvc.perform(put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        request
                )))
                //then
                .andExpect(status().isOk());
    }
}
