package org.example.expert.domain.comment.entity;

import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommentTest {

    @Test
    void 댓글_내용_업데이트_성공() {
        // given
        User user = new User("user@email.com", "password", UserRole.USER);
        Todo todo = new Todo("title", "contents", "weather", user);

        Comment comment = new Comment("old contents", user, todo);

        // when
        comment.update("new contents");

        // then
        assertThat(comment.getContents()).isEqualTo("new contents");
    }
}
