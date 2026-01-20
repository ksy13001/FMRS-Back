package com.ksy.fmrs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksy.fmrs.dto.player.FmPlayerDto;
import com.ksy.fmrs.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class FmPlayerJsonDirectoryReader {

    private final ObjectMapper objectMapper;

    public List<FmPlayerDto> getPlayersFromFmPlayers(String dirPath) {
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
}
