package com.ksy.fmrs.mapper;

import com.ksy.fmrs.domain.enums.MappingStatus;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.apiFootball.LeagueApiPlayersDto;
import com.ksy.fmrs.util.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class PlayerMapper {

    private static final String NATION_CHINA_PR      = "CHINA PR";
    private static final String NATION_KOREA_REPUBLIC = "KOREA REPUBLIC";


    public List<Player> leaguePlayersToEntities(LeagueApiPlayersDto leagueApiPlayersDto) {
        return leagueApiPlayersDto.response().stream()
                .filter(Objects::nonNull)
                .map(playerWrapperDto -> {
                    return createPlayer(playerWrapperDto.player());
                })
                .toList();
    }

    /**
     * API DTO → Player 엔티티 변환
     * <p>
     * ● 중국·한국 국적 선수는 <b>lastname</b> 필드 사용<br/>
     * ● 그 외 국적 선수는 <b>name</b> 필드 사용해 성 추출<br/>
     * ● 필수 키(first, last, birth, nation) 중 하나라도 누락 시 FAILED 상태로 생성
     */
    private Player createPlayer(LeagueApiPlayersDto.PlayerDto dto) {
        String rawNation = dto.nationality();
        String nation    = upperOrNull(rawNation);

        boolean eastAsianFormat = NATION_CHINA_PR.equalsIgnoreCase(nation) ||
                NATION_KOREA_REPUBLIC.equalsIgnoreCase(nation);

        // 한국 or 중국일 경우 lastname에서 성 가져오기
        String firstRaw = dto.firstname();
        String lastRaw  = eastAsianFormat ? dto.lastname() : dto.name();

        String first = upperOrNull(StringUtils.getFirstName(firstRaw));
        String last  = upperOrNull(StringUtils.getLastName(lastRaw));

        LocalDate birth = dto.birth() != null ? dto.birth().date() : null;

        MappingStatus status = (first == null || last == null || birth == null || nation == null)
                ? MappingStatus.FAILED
                : MappingStatus.UNMAPPED;

        return Player.builder()
                .playerApiId(dto.id())
                .firstName(first)
                .lastName(last)
                .birth(birth)
                .height(StringUtils.extractNumber(dto.height()))
                .weight(StringUtils.extractNumber(dto.weight()))
                .imageUrl(dto.photo())
                .nationName(nation)
                .mappingStatus(status)
                .build();
    }

    /** 대문자 변환 + null 안전 래퍼 */
    private String upperOrNull(String value) {
        return value == null ? null : value.toUpperCase();
    }
}