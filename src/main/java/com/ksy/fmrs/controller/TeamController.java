package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.team.TeamDetailsDto;
import com.ksy.fmrs.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class TeamController {
    private final TeamService teamService;


    @GetMapping("/api/teams/{id}")
    public TeamDetailsDto getTeamDetails(@PathVariable Long id) {
        return teamService.findTeamById(id);
    }

    @GetMapping("/api/teams/search/{name}")
    public List<TeamDetailsDto> getTeam(@PathVariable String name) {
        return teamService.findTeamsByName(name);
    }

}
