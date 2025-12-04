package org.example.expert.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.comment.dto.request.CommentSaveRequest;
import org.example.expert.domain.comment.dto.response.CommentResponse;
import org.example.expert.domain.comment.dto.response.CommentSaveResponse;
import org.example.expert.domain.comment.service.CommentService;
import org.example.expert.domain.common.dto.AuthUser;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @MockitoBean
    private AuthUserArgumentResolver argumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
    }

    @Test
    void 댓글저장성공() throws Exception {
        // given
        long todoId = 1L;
        CommentSaveRequest request = new CommentSaveRequest("contents");

        when(argumentResolver.supportsParameter(any())).thenReturn(true);
        when(argumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(authUser);

        when(commentService.saveComment(authUser, todoId, request))
                .thenReturn(new CommentSaveResponse(
                        1L,
                        "contents",
                        new UserDTO(authUser.getId(), authUser.getEmail())
                ));
        // when
        mockMvc.perform(post("/todos/{todoId}/comments", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isOk());

        verify(commentService).saveComment(
                any(AuthUser.class),
                eq(1L),
                any(CommentSaveRequest.class)
        );
    }

    @Test
    void 댓글조회성공() throws Exception {
        // given
        long todoId = 1L;

        when(commentService.getComments(todoId))
                .thenReturn(List.of(new CommentResponse(1L, "댓글1", new UserDTO(1L, "user1@email.com"))));
        //when
        mockMvc.perform(get("/todos/{todoId}/comments", todoId)
                        .contentType(MediaType.APPLICATION_JSON))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contents").value("댓글1"));

        verify(commentService).getComments(todoId);
    }
}