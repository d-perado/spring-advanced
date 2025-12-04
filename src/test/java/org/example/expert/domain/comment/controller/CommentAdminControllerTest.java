package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentAdminController.class)
class  CommentAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentAdminService commentAdminService;

    @Test
    void 댓글삭제_성공() throws Exception {
        //given
        long commentId = 1L;

        doNothing().when(commentAdminService).deleteComment(commentId);

        //when
        mockMvc.perform(delete("/admin/comments/{commentId}", commentId))
                .andExpect(status().isOk());

        //then
        verify(commentAdminService).deleteComment(commentId);
    }
}
