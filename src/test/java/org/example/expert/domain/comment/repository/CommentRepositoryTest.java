package org.example.expert.domain.comment.repository;

import org.assertj.core.api.Assertions;
import org.example.expert.domain.comment.entity.Comment;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void findByTodoIdWithUser_테스트() {
        //given
        User user = new User("asdf@naver.com", "password", UserRole.USER);
        User savedUser = userRepository.save(user);

        Todo todo = new Todo("title", "content", "weather", savedUser);
        Todo savedTodo = todoRepository.save(todo);

        Comment comment = new Comment("content", savedUser, savedTodo);
        commentRepository.save(comment);

        long todoId = savedTodo.getId();

        //when
        List<Comment> result = commentRepository.findByTodoIdWithUser(todoId);

        //then
        Assertions.assertThat(result)
                .hasSize(1)
                .allSatisfy(c -> {
                    Assertions.assertThat(c.getContents()).isEqualTo("content");
                    Assertions.assertThat(c.getUser().getEmail()).isEqualTo("asdf@naver.com");
                    Assertions.assertThat(c.getTodo().getId()).isEqualTo(todoId);
                });
    }
}
