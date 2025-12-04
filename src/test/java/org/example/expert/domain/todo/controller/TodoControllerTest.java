package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.UserDTO;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    private AuthUser authUser;

    @MockitoBean
    private AuthUserArgumentResolver argumentResolver;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
    }

    @Test
    void 일정_생성() throws Exception {
        // given
        UserDTO userDTO = new UserDTO(1L, "email@email.com");
        TodoSaveRequest request = new TodoSaveRequest("title", "content");
        TodoSaveResponse response = new TodoSaveResponse(1L, "title", "contents", "weather", userDTO);

        when(argumentResolver.supportsParameter(any())).thenReturn(true);
        when(argumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(authUser);

        when(todoService.saveTodo(any(), any())).thenReturn(response);

        //when
        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void 일정리스트_조회() throws Exception {
        // given
        int page = 0;
        int size = 10;

        UserDTO userDTO = new UserDTO(1L, "email@email.com");

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();

        TodoResponse todo1 = new TodoResponse(1L, "title1", "content1", "weather", userDTO, createdAt, modifiedAt);

        TodoResponse todo2 = new TodoResponse(2L, "title2", "content2", "weather", userDTO, createdAt, modifiedAt);

        Page<TodoResponse> mockedPage = new PageImpl<>(
                List.of(todo1, todo2),
                PageRequest.of(page, size),
                2
        );

        when(todoService.getTodos(page, size)).thenReturn(mockedPage);

        //when
        mockMvc.perform(get("/todos")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("title1"))
                .andExpect(jsonPath("$.content[0].contents").value("content1"))
                .andExpect(jsonPath("$.content[0].weather").value("weather"))
                .andExpect(jsonPath("$.content[0].user.id").value(1))
                .andExpect(jsonPath("$.content[0].user.email").value("email@email.com"))
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.content[0].modifiedAt").exists())
                .andExpect(jsonPath("$.page.totalElements").value(2));


    }

    @Test
    void 일정조회_성공() throws Exception {
        //given
        long todoId = 1L;
        long userId = 1L;
        UserDTO userDTO = new UserDTO(userId, "email@email.com");

        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime modifiedAt = LocalDateTime.now();

        TodoResponse response = new TodoResponse(todoId, "title", "content", "weather", userDTO, createdAt, modifiedAt);

        when(todoService.getTodo(todoId)).thenReturn(response);

        //when
        mockMvc.perform(get("/todos/{todoId}", todoId))
                .andExpect(status().isOk())
                //then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(todoId))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.contents").value("content"))
                .andExpect(jsonPath("$.weather").value("weather"))
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.user.email").value("email@email.com"));


    }
}