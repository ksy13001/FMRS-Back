package com.ksy.fmrs.service;

import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.mapper.FmPlayerMapper;
import com.ksy.fmrs.repository.Player.FmPlayerBulkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FmPlayerImportService {

    private final FmPlayerBulkRepository fmPlayerBulkRepository;
    private final FmPlayerMapper fmPlayerMapper;
    private final FmPlayerJsonDirectoryReader fmPlayerJsonDirectoryReader;
    @Value( "${fm.import.batch_size}")
    private int batchSize;

    public void saveFmPlayers(String dirPath, FmVersion fmVersion) {
        List<FmPlayerDto> fmPlayerDtos = fmPlayerJsonDirectoryReader.getPlayersFromFmPlayers(dirPath);
        List<FmPlayer> fmPlayers = fmPlayerMapper.toEntity(fmPlayerDtos, fmVersion);
        fmPlayerBulkRepository.bulkInsertFmPlayersInBatches(fmPlayers, batchSize);
    }
}
