package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private WeatherClient weatherClient;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    @Test
    void 일정_생성() {
        //given
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(weatherClient.getTodayWeather()).willReturn("weather");

        Todo todo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                "weather",
                user
        );

        given(todoRepository.save(any())).willReturn(todo);

        //when
        TodoSaveResponse result = todoService.saveTodo(authUser, todoSaveRequest);

        //then
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getContents()).isEqualTo("contents");
        assertThat(result.getUser().getId()).isEqualTo(1L);
    }

    @Test
    void 일정페이지_조회() {
        //given
        int page = 1;
        int size = 5;

        User user1 = new User("email1@email.com", "password1", UserRole.USER);
        User user2 = new User("email2@email.com", "password2", UserRole.USER);

        Todo todo1 = new Todo("title", "contents", "weather", user1);
        Todo todo2 = new Todo("title", "contents", "weather", user2);

        List<Todo> todoList = List.of(todo1, todo2);

        Pageable expectedPageable = PageRequest.of(page - 1, size);
        Page<Todo> todos = new PageImpl<>(todoList, expectedPageable, 2L);

        given(todoRepository.findAllByOrderByModifiedAtDesc(expectedPageable)).willReturn(todos);

        //when
        Page<TodoResponse> result = todoService.getTodos(page, size);

        //then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("title");
        assertThat(result.getContent().size()).isEqualTo(2);
    }

    @Test
    void 일정단건_조회_성공() {
        // given
        long todoId = 1L;

        User user = new User("email@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 10L);

        Todo todo = new Todo("title", "contents", "weather", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());

        given(todoRepository.findByIdWithUser(todoId))
                .willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.getTodo(todoId);

        // then
        assertThat(response.getId()).isEqualTo(todoId);
        assertThat(response.getTitle()).isEqualTo("title");
        assertThat(response.getContents()).isEqualTo("contents");
        assertThat(response.getWeather()).isEqualTo("weather");
        assertThat(response.getUser().getId()).isEqualTo(10L);
        assertThat(response.getUser().getEmail()).isEqualTo("email@test.com");
    }

}
