package com.ksy.fmrs.domain.player;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.PlayerMappingStatus;
import com.ksy.fmrs.dto.apiFootball.LeagueApiPlayersDto;
import com.ksy.fmrs.util.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component
public class PlayerMapper {

    public List<Player> leaguePlayersToEntities(LeagueApiPlayersDto leagueApiPlayersDto) {
        return leagueApiPlayersDto.response().stream()
                .filter(Objects::nonNull)
                .map(playerWrapperDto -> {
                    return createPlayer(playerWrapperDto.player());
                })
                .toList();
    }

    private Player createPlayer(LeagueApiPlayersDto.PlayerDto playerDto) {
        // 원시 값 추출
        String firstNameRaw = StringUtils.getFirstName(playerDto.firstname());
        String lastNameRaw  = StringUtils.getLastName(playerDto.name());
        LocalDate birth     = playerDto.birth().date();
        String nationNameRaw= playerDto.nationality();

        // 대문자 변환 및 null 처리
        String firstName   = (firstNameRaw  != null) ? firstNameRaw.toUpperCase()  : null;
        String lastName    = (lastNameRaw   != null) ? lastNameRaw.toUpperCase()   : null;
        String nationName  = (nationNameRaw!= null) ? nationNameRaw.toUpperCase(): null;

        // 필수 필드 누락 시 FAILED 상태
        PlayerMappingStatus status = (
                firstName == null ||
                        lastName  == null ||
                        birth     == null ||
                        nationName== null
        ) ? PlayerMappingStatus.FAILED : PlayerMappingStatus.UNMAPPED;

        // 엔티티 생성
        return Player.builder()
                .playerApiId(playerDto.id())
                .firstName(firstName)
                .lastName(lastName)
                .birth(birth)
                .height(StringUtils.extractNumber(playerDto.height()))
                .weight(StringUtils.extractNumber(playerDto.weight()))
                .imageUrl(playerDto.photo())
                .nationName(nationName)
                .mappingStatus(status)
                .build();
    }
}
