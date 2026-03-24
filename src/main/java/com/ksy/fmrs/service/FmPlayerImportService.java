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
        if (files == null || files.length == 0) {
            log.warn("JSON 파일 없음: {}", dirPath);
            return;
        }
        int total = files.length;
        log.info("{} files found", total);
        for (int i = 0; i < total; i += batchSize) {
            int end = Math.min(total, i + batchSize);
            List<FmPlayerDto> fmPlayerDtos = fmPlayerJsonDirectoryReader.readFiles(Arrays.copyOfRange(files, i, end));
            List<FmPlayer> fmPlayers = fmPlayerMapper.toEntity(fmPlayerDtos,  fmVersion);
            fmPlayerBulkRepository.bulkInsertFmPlayer(fmPlayers);

            log.info("처리 완료: {}/{}", end, total);
        }

    }
}
