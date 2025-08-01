package com.ksy.fmrs.repository;

import com.ksy.fmrs.config.TestQueryDSLConfig;
import com.ksy.fmrs.config.TestTimeProviderConfig;
import com.ksy.fmrs.domain.Comment;
import com.ksy.fmrs.domain.User;
import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.enums.Role;
import com.ksy.fmrs.domain.player.Player;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Import({TestQueryDSLConfig.class, TestTimeProviderConfig.class})
@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private TestEntityManager tem;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("player comment 갯수 세기")
    void comment_countByPlayerId(){
        // given
        int totalCount = 10;
        User user = User.builder().username("u1").password("pw").role(Role.ROLE_USER).build();
        Player player = Player.builder().build();
        tem.persistAndFlush(user);
        tem.persistAndFlush(player);

        for (int i = 0; i < totalCount; i++) {
            tem.persistAndFlush(Comment.of(user, player, "con"));
        }

        // when
        int actualCount = commentRepository.countByPlayerId(player.getId());

        // then
        Assertions.assertThat(actualCount).isEqualTo(totalCount);
    }
}