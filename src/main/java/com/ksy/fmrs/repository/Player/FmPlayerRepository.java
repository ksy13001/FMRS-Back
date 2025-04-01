package com.ksy.fmrs.repository.Player;


import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.domain.player.QFmPlayer;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class FmPlayerRepository {
    private final JPAQueryFactory  jpaQueryFactory;

    public List<FmPlayer> findFmPlayerByFirstNameAndLastNameAndBirthAndNationName(String firstName, String lastName, LocalDate birth, String nationName) {
        return jpaQueryFactory.selectFrom(QFmPlayer.fmPlayer)
                .where(eqFirstName(firstName), eqLastName(lastName), eqBirth(birth), eqNation(nationName))
                .fetch();
    }

    private BooleanExpression eqFirstName(String firstName) {
        if  (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("firstName is null or empty");
        }
        return QFmPlayer.fmPlayer.firstName.eq(firstName);
    }

    private BooleanExpression eqLastName(String lastName) {
        if  (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("lastName is null or empty");
        }
        return QFmPlayer.fmPlayer.lastName.eq(lastName);
    }

    private BooleanExpression eqBirth(LocalDate birth) {
        if  (birth == null) {
            throw new IllegalArgumentException("birth is null or empty");
        }
        return  QFmPlayer.fmPlayer.birth.eq(birth);
    }

    private BooleanExpression eqNation(String nationName) {
        if (nationName == null || nationName.isEmpty()) {
            throw new IllegalArgumentException("nationName is null or empty");
        }
        return QFmPlayer.fmPlayer.nationName.eq(nationName);
    }



}
