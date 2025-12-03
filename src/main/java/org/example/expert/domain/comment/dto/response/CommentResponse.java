package org.example.expert.domain.comment.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.UserDTO;

@Getter
public class CommentResponse {

    private final Long id;
    private final String contents;
    private final UserDTO user;

    public CommentResponse(Long id, String contents, UserDTO user) {
        this.id = id;
        this.contents = contents;
        this.user = user;
    }
}
