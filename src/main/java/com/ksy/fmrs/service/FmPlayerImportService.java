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
    private final MappingService mappingService;
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
        linkNewVersionRowsToAlreadyMappedPlayers(fmVersion);
    }

    // 이미 MATCHED 된 선수의 fm_uid로 새 버전(fmVersion) row가 들어온 경우,
    // 매핑 job을 기다리지 않고 바로 player_id를 연결하고 latest_* 를 최신화한다.
    private void linkNewVersionRowsToAlreadyMappedPlayers(FmVersion fmVersion) {
        int propagatedFmPlayerRows = mappingService.propagatePlayerIdByFmUid();
        log.info("fm_uid 기존 매핑 전파: {} rows (fmVersion={})", propagatedFmPlayerRows, fmVersion);

        int refreshedPlayers = mappingService.refreshLatestFmData();
        log.info("latest_* 갱신된 선수 수: {} (fmVersion={})", refreshedPlayers, fmVersion);
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
