package com.ksy.fmrs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.domain.enums.FmVersion;
import com.ksy.fmrs.domain.player.FmPlayer;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.repository.BulkRepository;
import com.ksy.fmrs.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FmPlayerImportService {

    private final ObjectMapper objectMapper;
    private final BulkRepository bulkRepository;
    private static final int CHUNK_SIZE = 1000;

    public void saveFmPlayers(String dirPath, FmVersion fmVersion) {
        List<FmPlayerDto> fmPlayerDtos = getPlayersFromFmPlayers(dirPath);
        log.info("fmPlayer 저장 시작: {}", fmPlayerDtos.size());
        List<FmPlayer> fmPlayers = new ArrayList<>();
        fmPlayerDtos.forEach(fmPlayer -> {
            fmPlayers.add(FmPlayer.FmPlayerDtoToEntity(fmPlayer, fmVersion));
        });

        bulkInsertFmPlayer(fmPlayers);
    }


    private List<FmPlayerDto> getPlayersFromFmPlayers(String dirPath) {
        log.info("dir 위치 : {}",dirPath);
        File folder = new File(dirPath);
        File[] jsonFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));

        return Arrays.stream(Objects.requireNonNull(jsonFiles))
                .map(file -> {
                    try {
                        FmPlayerDto dto = objectMapper.readValue(file, FmPlayerDto.class);
                        dto.setName(StringUtils.getPlayerNameFromFileName(file.getName().toUpperCase()));
                        return dto;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .collect(Collectors.toList());
    }

    private void bulkInsertFmPlayer(List<FmPlayer> fmPlayers){
        int total = fmPlayers.size();
        // 1000개씩 bulk insert
        for (int i = 0; i < total; i += CHUNK_SIZE) {
            int end = Math.min(i + CHUNK_SIZE, total);
            List<FmPlayer> now = fmPlayers.subList(i, end);
            bulkRepository.bulkInsertFmPlayers(now);
        }
    }

}
