package com.ksy.fmrs.dto.team;

import com.ksy.fmrs.domain.Team;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TeamDetailsDto {
    private Long id;
    private String teamName;
    private String teamLogo;

    public TeamDetailsDto(Team team) {
        this.id = team.getId();
        this.teamName = team.getName();
        this.teamLogo = team.getLogoUrl();
    }
}
