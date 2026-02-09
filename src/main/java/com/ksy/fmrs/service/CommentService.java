package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Comment;
import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.PaginationDto;
import com.ksy.fmrs.dto.comment.CommentCountResponseDto;
import com.ksy.fmrs.dto.comment.CommentListResponseDto;
import com.ksy.fmrs.dto.comment.CommentResponseDto;
import com.ksy.fmrs.exception.NotCommentOwnerException;
import com.ksy.fmrs.repository.CommentRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final PlayerRepository playerRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDto save(Long userId, Long playerId, String content) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new EntityNotFoundException("player not found"));

        Comment comment = Comment.of(user, player, content);
        commentRepository.save(comment);
        return new CommentResponseDto(playerId, userId, user.getUsername(), comment);
    }

    @Transactional(readOnly = true)
    public CommentListResponseDto getPlayerComments(Long playerId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByPlayerId(playerId, pageable);
        return new CommentListResponseDto(
                getComments(comments),
                new PaginationDto(comments)
        );
    }

    @Transactional
    public void delete(Long commentId, Long userId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("comment not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        if (!comment.getUser().equals(user)) {
            throw new NotCommentOwnerException("this user is not comment owner");
        }

        if (comment.isDeleted()) {
            throw new IllegalArgumentException("comment is already deleted");
        }

        comment.deleteComment();
    }

    @Transactional(readOnly = true)
    public CommentCountResponseDto getCommentCountByPlayerId(Long playerId) {
        return new CommentCountResponseDto(commentRepository.countByPlayerId(playerId));
    }


    private List<CommentResponseDto> getComments(Page<Comment> comments) {
        return comments.stream()
                .map(comment -> {
                    return new CommentResponseDto(
                            comment.getPlayer().getId(),
                            comment.getUser().getId(),
                            comment.getUser().getUsername(),
                            comment);
                }).toList();
    }
}
