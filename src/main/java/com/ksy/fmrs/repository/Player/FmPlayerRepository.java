package com.ksy.fmrs.repository.Player;

import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.dto.BirthNationKey;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static com.ksy.fmrs.domain.player.QFmPlayer.fmPlayer;

@RequiredArgsConstructor
@Repository
public class FmPlayerRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public List<FmPlayer> findFmPlayerByFirstNameAndLastNameAndBirthAndNationName(String firstName, String lastName, LocalDate birth, String nationName) {
        return jpaQueryFactory.selectFrom(fmPlayer)
                .where(eqFirstName(firstName), eqLastName(lastName), eqBirth(birth), eqNation(nationName))
                .fetch();
    }

    public List<FmPlayer> findFmPlayerByBirthAndNation(LocalDate birth, String nationName) {
        return jpaQueryFactory.selectFrom(fmPlayer)
                .where(eqBirth(birth), eqNation(nationName), fmPlayer.player.isNull())
                .fetch();
    }

    public List<FmPlayer> findUnlinkedFmPlayersByBirthNationKeys(Collection<BirthNationKey> keys) {
        BooleanBuilder keyCondition = new BooleanBuilder();

        keys.stream()
                .filter(BirthNationKey::isComplete)
                .forEach(key -> keyCondition.or(
                        fmPlayer.birth.eq(key.birth())
                                .and(fmPlayer.nationName.eq(key.nationName()))
                ));

        if (!keyCondition.hasValue()) {
            return List.of();
        }

        return jpaQueryFactory.selectFrom(fmPlayer)
                .where(fmPlayer.player.isNull(), keyCondition)
                .fetch();
    }

    private BooleanExpression eqFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            throw new IllegalArgumentException("firstName is null or empty");
        }
        return fmPlayer.firstName.eq(firstName);
    }

    private BooleanExpression eqLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            throw new IllegalArgumentException("lastName is null or empty");
        }
        return fmPlayer.lastName.eq(lastName);
    }

    private BooleanExpression eqBirth(LocalDate birth) {
        if (birth == null) {
            throw new IllegalArgumentException("birth is null or empty");
        }
        return fmPlayer.birth.eq(birth);
    }

    private BooleanExpression eqNation(String nationName) {
        if (nationName == null || nationName.isEmpty()) {
            throw new IllegalArgumentException("nationName is null or empty");
        }
        return fmPlayer.nationName.eq(nationName);
    }
}
