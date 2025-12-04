package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
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
    void todo를_생성한다() {
        //given
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("title", "contents");
        AuthUser authUser = new AuthUser(1L, "email", UserRole.USER);
        User user = User.fromAuthUser(authUser);

        given(weatherClient.getTodayWeather()).willReturn("weather");

        Todo mockTodo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                "weather",
                user
        );

        given(todoRepository.save(any())).willReturn(mockTodo);

        //when
        TodoSaveResponse result = todoService.saveTodo(authUser, todoSaveRequest);

        //then
        assertThat(result.getTitle()).isEqualTo("title");
        assertThat(result.getContents()).isEqualTo("contents");
        assertThat(result.getUser().getId()).isEqualTo(1L);
    }

}
