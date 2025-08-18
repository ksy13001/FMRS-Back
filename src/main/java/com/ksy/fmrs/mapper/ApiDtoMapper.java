package com.ksy.fmrs.mapper;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.dto.apiFootball.TeamListApiResponseDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApiDtoMapper {

    public List<Team> toEntity(TeamListApiResponseDto teamListApiResponseDto) {
        if (teamListApiResponseDto == null || teamListApiResponseDto.response() == null || teamListApiResponseDto.response().isEmpty()) {
            return null;
        }

        List<Team> teams = new ArrayList<>();
        teamListApiResponseDto.response().forEach(response -> {
            teams.add(Team.builder()
                    .name(response.team().name())
                    .logoUrl(response.team().logo())
                    .teamApiId(response.team().id())
                    .build());
        });
        return teams;
    }
}
