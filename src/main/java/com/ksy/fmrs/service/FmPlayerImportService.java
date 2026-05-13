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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
        File[] files = fmPlayerJsonDirectoryReader.listJsonFiles(dirPath);
        if (hasNoFiles(files)) {
            log.warn("JSON 파일 없음: {}", dirPath);
            return;
        }
        log.info("{} files found", files.length);
        saveFmPlayersInBatches(files, fmVersion);
    }

    private boolean hasNoFiles(File[] files) {
        return files == null || files.length == 0;
    }

    private void saveFmPlayersInBatches(File[] files, FmVersion fmVersion) {
        int total = files.length;
        for (int start = 0; start < total; start += batchSize) {
            int end = Math.min(total, start + batchSize);
            saveFmPlayerBatch(files, start, end, fmVersion);
            log.info("처리 완료: {}/{}", end, total);
        }
    }

    private void saveFmPlayerBatch(File[] files, int start, int end, FmVersion fmVersion) {
        List<FmPlayerDto> fmPlayerDtos = readFmPlayerDtos(files, start, end);
        List<FmPlayer> fmPlayers = fmPlayerMapper.toEntity(fmPlayerDtos, fmVersion);
        fmPlayerBulkRepository.bulkInsertFmPlayer(fmPlayers);
    }

    private List<FmPlayerDto> readFmPlayerDtos(File[] files, int start, int end) {
        return fmPlayerJsonDirectoryReader.readFiles(Arrays.copyOfRange(files, start, end));
    }
}
