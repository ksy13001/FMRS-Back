package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Comment;
import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.comment.CommentListResponseDto;
import com.ksy.fmrs.dto.comment.CommentRequestDto;
import com.ksy.fmrs.dto.comment.CommentResponseDto;
import com.ksy.fmrs.repository.CommentRepository;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        if (content.length() > 500 || content == null) {
            throw new IllegalArgumentException("content length must be less than 500");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("user not found"));
        Player player = playerRepository.findById(playerId).orElseThrow(() -> new EntityNotFoundException("player not found"));

        Comment comment = Comment.of(user, player, content);
        commentRepository.save(comment);
        return new CommentResponseDto(playerId, userId, user.getUsername(), comment);
    }

    @Transactional(readOnly = true)
    public CommentListResponseDto getPlayerComments(Long playerId) {

        List<Comment> comments = commentRepository.findByPlayerId(playerId);
        return new CommentListResponseDto(comments.stream()
                .map(comment -> {
                    return new CommentResponseDto(
                            comment.getPlayer().getId(),
                            comment.getUser().getId(),
                            comment.getUser().getUsername(),
                            comment);
                }).toList());
    }

}
