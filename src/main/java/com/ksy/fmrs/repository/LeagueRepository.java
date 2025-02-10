package com.ksy.fmrs.repository;

import com.ksy.fmrs.domain.League;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.stereotype.Repository;

@Repository
public interface LeagueRepository extends JpaAttributeConverter<League, Long> {
}
