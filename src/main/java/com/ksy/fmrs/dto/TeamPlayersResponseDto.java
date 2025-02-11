package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.Player;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
public class TeamPlayersResponseDto {
    private final List<PlayerDetailsResponse> players;
}
