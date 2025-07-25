package com.ksy.fmrs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.Comment;
import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.comment.CommentRequestDto;
import com.ksy.fmrs.dto.comment.CommentResponseDto;
import com.ksy.fmrs.security.CustomUserDetails;
import com.ksy.fmrs.security.JwtFilter;
import com.ksy.fmrs.security.JwtTokenProvider;
import com.ksy.fmrs.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtFilter jwtFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private CommentService commentService;

    private static final String AUTH_USERNAME = "auth_user";
    private static final String AUTH_PW = "auth_pw";

    private Player player;
    private User authUser;

    @BeforeEach
    void setup() {
        Long playerId = 456L;
        Long userId =  200L;

        player = createPlayer();
        ReflectionTestUtils.setField(player, "id", playerId);

        authUser = createUser(AUTH_USERNAME, AUTH_PW);
        ReflectionTestUtils.setField(authUser, "id", userId);

    }

//    @Test
//    @DisplayName("인증받은 사용자가 500자 이하 댓글 달면, 등록")
//    void comment_success() throws Exception {
//        // given
//        Long playerId = player.getId();
//        Long userId = authUser.getId();
//        Long commentId = 100L;
//        String content = "SIUUUUUUUUUUUUU";
//        Comment comment = Comment.of(authUser, player, content);
//        ReflectionTestUtils.setField(comment, "id", commentId);
//        CustomUserDetails userDetails = new CustomUserDetails(authUser);
//
//        CommentResponseDto commentResponseDto = new CommentResponseDto(
//                playerId, userId, AUTH_USERNAME, comment
//        );
//
//        given(commentService.save(anyLong(), anyLong(), anyString()))
//                .willReturn(commentResponseDto);
//
//        // when
//        ResultActions actions = mvc.perform(post("/api/players/{playerId}/comments", playerId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(new CommentRequestDto(content)))
//                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
//                .with(csrf())
//        );
//
//        // then
//        actions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.data.commentId").value(commentId))
//                .andExpect(jsonPath("$.data.playerId").value(player.getId()))
//                .andExpect(jsonPath("$.data.userId").value(authUser.getId()))
//                .andExpect(jsonPath("$.data.username").value(authUser.getUsername())).andExpect(jsonPath("$.data.username").value(authUser.getUsername()))
//                .andExpect(jsonPath("$.data.content").value(content));
//    }
//

    private Player createPlayer() {
        return Player.builder()
                .build();
    }

    private User createUser(String username, String password) {
        return User.builder()
                .username(username)
                .password(password)
                .build();
    }
}