package org.example.expert.domain.todo.entity;

import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TodoTest {

    @Test
    void 일정_업데이트() {
        //given
        User user = new User("email@email.com", "password", UserRole.USER);
        Todo oldTodo = new Todo("oldTitle", "oldContents", "weather", user);

        //when
        oldTodo.update("newTitle","newContents");
        //then
        assertThat(oldTodo.getTitle()).isEqualTo("newTitle");
        assertThat(oldTodo.getContents()).isEqualTo("newContents");
    }
}
