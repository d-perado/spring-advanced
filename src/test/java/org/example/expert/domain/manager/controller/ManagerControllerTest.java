package org.example.expert.domain.manager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.UserDTO;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManagerController.class)
public class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ManagerService managerService;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
    }
    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private AuthUserArgumentResolver argumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 매니저생성_성공() throws Exception {
        // given
        long todoId = 1L;
        long userId = 1L;

        UserDTO userDTO = new UserDTO(userId, "email@email.com");

        when(argumentResolver.supportsParameter(any())).thenReturn(true);
        when(argumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(authUser);

        ManagerSaveRequest request = new ManagerSaveRequest(10L);

        ManagerSaveResponse response = new ManagerSaveResponse(1L, userDTO);
        when(managerService.saveManager(any(), eq(todoId), any()))
                .thenReturn(response);

        //when
        mockMvc.perform(post("/todos/{todoId}/managers", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.user.email").value("email@email.com"));
    }

    @Test
    void 매니저조회_성공() throws Exception {
        // given
        long todoId = 1L;

        List<ManagerResponse> responses = List.of(
                new ManagerResponse(1L, new UserDTO(1L, "user1@email.com")),
                new ManagerResponse(2L, new UserDTO(2L, "user2@email.com"))
        );

        when(managerService.getManagers(todoId)).thenReturn(responses);

        //when
        mockMvc.perform(get("/todos/{todoId}/managers", todoId)
                        .contentType(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].user.id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].user.email").value("user2@email.com"));
    }

    @Test
    void 매니저삭제_성공() throws Exception {
        // given
        long todoId = 1L;
        long managerId = 2L;
        long userId = 1L;

        String token = "Bearer fake.jwt.token";

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn(String.valueOf(userId));

        when(jwtUtil.extractClaims("fake.jwt.token")).thenReturn(claims);

        doNothing().when(managerService).deleteManager(userId, todoId, managerId);

        //when
        mockMvc.perform(delete("/todos/{todoId}/managers/{managerId}", todoId, managerId)
                        .header("Authorization", token))
                //then
                .andExpect(status().isOk());
    }
}

