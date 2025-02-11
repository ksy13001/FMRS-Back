package com.ksy.fmrs.service;

import com.ksy.fmrs.repository.Team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    // 팀 소속 선수들 조회

}
