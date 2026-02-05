package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Comment;
import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.PaginationDto;
import com.ksy.fmrs.dto.comment.CommentListResponseDto;
import com.ksy.fmrs.exception.NotCommentOwnerException;
import com.ksy.fmrs.repository.CommentRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    private Player player;

    private User user;

    @BeforeEach
    void setUp() {
        this.player = createPlayer();
        this.user = createUser("user", "pw");
    }

    @Test
    @DisplayName("500자 이하 댓글 등록 유효, Comment는 등록시 Player, User 있어야함")
    void save_success() {
        // given
        Long userId = 123L;
        Long playerId = 100L;
        String content = "SIUUUUUUUUUU";


        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));
        given(playerRepository.findById(playerId))
                .willReturn(Optional.of(player));
        // when
        commentService.save(userId, playerId, content);
        ArgumentCaptor<Comment> savedComment = ArgumentCaptor.forClass(Comment.class);

        // then
        verify(commentRepository, times(1)).save(savedComment.capture());
        Assertions.assertThat(savedComment.getValue().getContent()).isEqualTo(content);

    }

    @Test
    @DisplayName("500자 이상일 경우 예외 처리")
    void save_fail_invalid_input() {
        // given
        String invalidContent = "100".repeat(500);

        // when & then
        Assertions.assertThatThrownBy(()->commentService.save(123L, 100L, invalidContent))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("첫페이지 댓글 조회시, Comment 리스트와 페이지 정보 반환")
    void get_comment_first_page_success(){
        // given
        Long userId = 123L;
        int total = 500;
        int pageSize = 10;
        List<Comment> data = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            data.add(Comment.of(user, player, String.valueOf(i)));
        }
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Comment> comments = new PageImpl<>(data, pageable, total);

        given(commentRepository.findByPlayerId(userId, pageable))
                .willReturn(comments);
        // when
        CommentListResponseDto actual =
                commentService.getPlayerComments(userId, pageable);
        // then
        Assertions.assertThat(actual.getPagination().getTotalElements())
                .isEqualTo(data.size());
        Assertions.assertThat(actual.getPagination().getSize())
                .isEqualTo(10);
        Assertions.assertThat(actual.getPagination().getTotalPages())
                .isEqualTo(total/pageSize);
        Assertions.assertThat(actual.getPagination().isFirst())
                .isEqualTo(true);
        Assertions.assertThat(actual.getPagination().isLast())
                .isEqualTo(false);
        Assertions.assertThat(actual.getPagination().isHasNext())
                .isEqualTo(true);
        Assertions.assertThat(actual.getPagination().isHasPrevious())
                .isEqualTo(false);
    }

    @Test
    @DisplayName("마지막 페이지 댓글 조회시, Comment 리스트와 페이지 정보 반환")
    void get_comment_last_page_success(){
        // given
        Long userId = 123L;
        int total = 500;
        int pageSize = 10;
        List<Comment> data = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            data.add(Comment.of(user, player, String.valueOf(i)));
        }
        Pageable pageable = PageRequest.of(49, pageSize);
        Page<Comment> comments = new PageImpl<>(data, pageable, total);

        given(commentRepository.findByPlayerId(userId, pageable))
                .willReturn(comments);
        // when
        CommentListResponseDto actual =
                commentService.getPlayerComments(userId, pageable);
        // then
        Assertions.assertThat(actual.getPagination().getTotalElements())
                .isEqualTo(data.size());
        Assertions.assertThat(actual.getPagination().getSize())
                .isEqualTo(10);
        Assertions.assertThat(actual.getPagination().getTotalPages())
                .isEqualTo(total/pageSize);
        Assertions.assertThat(actual.getPagination().isFirst())
                .isEqualTo(false);
        Assertions.assertThat(actual.getPagination().isLast())
                .isEqualTo(true);
        Assertions.assertThat(actual.getPagination().isHasNext())
                .isEqualTo(false);
        Assertions.assertThat(actual.getPagination().isHasPrevious())
                .isEqualTo(true);
    }

    @Test
    @DisplayName("중간 페이지 댓글 조회시, Comment 리스트와 페이지 정보 반환")
    void get_comment_middle_page_success(){
        // given
        Long userId = 123L;
        int total = 500;
        int pageSize = 10;
        List<Comment> data = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            data.add(Comment.of(user, player, String.valueOf(i)));
        }
        Pageable pageable = PageRequest.of(10, pageSize);
        Page<Comment> comments = new PageImpl<>(data, pageable, total);

        given(commentRepository.findByPlayerId(userId, pageable))
                .willReturn(comments);
        // when
        CommentListResponseDto actual =
                commentService.getPlayerComments(userId, pageable);
        // then
        Assertions.assertThat(actual.getPagination().getTotalElements())
                .isEqualTo(data.size());
        Assertions.assertThat(actual.getPagination().getSize())
                .isEqualTo(10);
        Assertions.assertThat(actual.getPagination().getTotalPages())
                .isEqualTo(total/pageSize);
        Assertions.assertThat(actual.getPagination().isFirst())
                .isEqualTo(false);
        Assertions.assertThat(actual.getPagination().isLast())
                .isEqualTo(false);
        Assertions.assertThat(actual.getPagination().isHasNext())
                .isEqualTo(true);
        Assertions.assertThat(actual.getPagination().isHasPrevious())
                .isEqualTo(true);
    }
    @Test
    @DisplayName("comment 삭제 시, deleted=ture")
    void delete_comment_success(){
        // given
        Long commentId = 123L;
        Comment comment = Comment.of(user, player, "hello");
        given(userRepository.findById(user.getId()))
                .willReturn(Optional.of(user));
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));

        // when
        Assertions.assertThat(comment.isDeleted())
                .isEqualTo(false);

        commentService.delete(commentId, user.getId());

        // then
        Assertions.assertThat(comment.isDeleted())
                .isEqualTo(true);
    }

    @Test
    @DisplayName("댓글 작성자와 삭제 요청자가 다르면 예외 발생")
    void  delete_comment_fail_not_owner(){
        // given
        User badUser = createUser("badUser", "badPw");
        Long badUserId = 36L;
        Long commentId = 123L;
        Comment comment = Comment.of(user, player, "hello");
        given(userRepository.findById(badUserId))
                .willReturn(Optional.of(badUser));
        given(commentRepository.findById(commentId))
                .willReturn(Optional.of(comment));
        // when && then
        Assertions.assertThatThrownBy(()->commentService.delete(commentId, badUserId))
                .isInstanceOf(NotCommentOwnerException.class);
    }

    public Player createPlayer() {
        return Player.builder().build();
    }

    public User createUser(String username, String password) {
        return User.builder()
                .username(username)
                .password(password)
                .build();
    }
}