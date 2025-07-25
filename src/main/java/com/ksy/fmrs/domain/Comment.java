package com.ksy.fmrs.domain;

import com.ksy.fmrs.domain.player.Player;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.security.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Comment extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    private boolean deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;


    public static Comment of(User user, Player player, String content){
        Comment comment = new Comment();
        comment.content = content;
        comment.user = user;
        comment.player = player;
        comment.user.getComments().add(comment);
        comment.player.getComments().add(comment);
        return comment;
    }

    public void deleteComment() {
        this.deleted = true;
    }

//    public void updateUser(User user) {
//        this.user = user;
//        user.getComments().add(this);
//    }
//
//    public void updatePlayer(Player player) {
//        this.player = player;
//        player.getComments().add(this);
//    }
}
