package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.domain.player.Transfer;
import com.ksy.fmrs.dto.transfer.TransferRequestDto;
import com.ksy.fmrs.exception.RetiredPlayerException;
import com.ksy.fmrs.repository.Player.PlayerRepository;
import com.ksy.fmrs.repository.Team.TeamRepository;
import com.ksy.fmrs.repository.TransferRepositoryJDBC;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class TransferService {
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final TransferRepositoryJDBC transferRepositoryJDBC;

    @Transactional
    public void saveAll(List<TransferRequestDto> dtos) {
        Set<Integer> playerApiIds = extractPlayerApiIds(dtos);
        Set<Integer> teamApiIds = extractTeamApiIds(dtos);

        Map<Integer, Player> playerMap = prefetchPlayers(playerApiIds);
        Map<Integer, Team> teamMap = prefetchTeams(teamApiIds);

        List<Transfer> transfers = dtos.stream()
                .filter(dto -> isExistEntity(playerMap, teamMap, dto))
                .map(dto -> recordTransfer(playerMap, teamMap, dto))
                .toList();

        transferRepositoryJDBC.saveAllOPKU(transfers);
    }

    private Set<Integer> extractPlayerApiIds(List<TransferRequestDto> dtos) {
        return dtos.stream().map(TransferRequestDto::playerApiId).collect(Collectors.toSet());
    }

    private Set<Integer> extractTeamApiIds(List<TransferRequestDto> dtos) {
        return dtos.stream()
                .flatMap(dto -> Stream.of(dto.fromTeamApiId(), dto.toTeamApiId()))
                .collect(Collectors.toSet());
    }

    private Map<Integer, Player> prefetchPlayers(Set<Integer> playerApiIds) {
        return playerRepository.findByPlayerApiIdIn(playerApiIds)
                .stream()
                .collect(Collectors.toMap(Player::getPlayerApiId, player -> player));
    }

    private Map<Integer, Team> prefetchTeams(Set<Integer> teamApiIds) {
        return teamRepository.findByTeamApiIdIn(teamApiIds)
                .stream()
                .collect(Collectors.toMap(Team::getTeamApiId, team -> team));
    }

    private boolean isExistEntity(Map<Integer, Player> playerMap, Map<Integer, Team> teamMap, TransferRequestDto dto) {
        return playerMap.containsKey(dto.playerApiId())
                && teamMap.containsKey(dto.fromTeamApiId())
                && teamMap.containsKey(dto.toTeamApiId());
    }

    private Transfer recordTransfer(Map<Integer, Player> playerMap, Map<Integer, Team> teamMap, TransferRequestDto dto) {
        return playerMap.get(dto.playerApiId())
                .recordTransfer(teamMap.get(dto.fromTeamApiId()), teamMap.get(dto.toTeamApiId()), dto.transferType(), dto.fee(), dto.currency(), dto.date(), dto.update());

    }

    @Transactional
    public void saveAllV1(List<TransferRequestDto> dtos) {
        List<Transfer> transfers = dtos.stream()
                .map(this::dtoToEntity)
                .toList();
        transferRepositoryJDBC.saveAllOPKU(transfers);
    }

    private Transfer dtoToEntity(TransferRequestDto transferRequestDto) {
        Player player = playerRepository.findByPlayerApiId(transferRequestDto.playerApiId())
                .orElseThrow(() -> new RetiredPlayerException("player not found"));
        Team fromTeam = teamRepository.findByTeamApiId(transferRequestDto.fromTeamApiId())
                .orElseThrow(() -> new EntityNotFoundException("fromTeam not found"));
        Team toTeam = teamRepository.findByTeamApiId(transferRequestDto.toTeamApiId())
                .orElseThrow(() -> new EntityNotFoundException("toTeam not found"));

        return player.recordTransfer(
                fromTeam, toTeam, transferRequestDto.transferType(), transferRequestDto.fee(), transferRequestDto.currency(), transferRequestDto.date(), transferRequestDto.update()
        );
    }
}
