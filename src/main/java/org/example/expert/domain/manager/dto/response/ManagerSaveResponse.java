package org.example.expert.domain.manager.dto.response;

import lombok.Getter;
import org.example.expert.domain.user.dto.UserDTO;

@Getter
public class ManagerSaveResponse {

    private final Long id;
    private final UserDTO user;

    public ManagerSaveResponse(Long id, UserDTO user) {
        this.id = id;
        this.user = user;
    }
}
