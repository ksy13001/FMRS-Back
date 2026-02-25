package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPlayerId(Long playerId, Pageable pageable);
    @Query("SELECT COUNT(c.id) FROM Comment c WHERE c.player.id = :playerId")
    int countByPlayerId(Long playerId);
}
