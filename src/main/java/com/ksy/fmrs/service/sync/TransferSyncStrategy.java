package com.ksy.fmrs.service.sync;

import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.SyncType;
import com.ksy.fmrs.dto.apiFootball.ApiFootballTransfers;
import com.ksy.fmrs.dto.transfer.TransferRequestDto;
import com.ksy.fmrs.mapper.ApiFootballMapper;
import com.ksy.fmrs.service.ApiFootballClient;
import com.ksy.fmrs.service.ApiFootballValidator;
import com.ksy.fmrs.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TransferSyncStrategy implements SyncStrategy<Team, ApiFootballTransfers, TransferRequestDto> {

    private final ApiFootballClient apiFootballClient;
    private final ApiFootballValidator apiFootballValidator;
    private final ApiFootballMapper apiFootballMapper;
    private final TransferService transferService;

    @Override
    public SyncType getSyncType() {
        return SyncType.TRANSFER;
    }

    @Override
    public Integer getSyncApiId(Team key) {
        return key.getTeamApiId();
    }

    @Override
    public List<ApiFootballTransfers> requestSportsData(Team key) {
        return List.of(apiFootballClient.requestTransfers(key.getTeamApiId()));
    }

    @Override
    public void validate(List<ApiFootballTransfers> dto) {
        apiFootballValidator.validateTransfer(dto.getFirst());
    }

    @Override
    public List<TransferRequestDto> transformToTarget(List<ApiFootballTransfers> dto) {
        return apiFootballMapper.toDto(dto.getFirst());
    }

    @Override
    public void persist(List<TransferRequestDto> dtos, Team key) {
        transferService.saveAll(dtos);
    }
}
