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


    public File[] listJsonFiles(String dirPath) {
        File folder = new File(dirPath);
        return folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
    }

    public List<FmPlayerDto> readFiles(File[] chunk){
        return Arrays.stream(Objects.requireNonNull(chunk))
                .map(file -> {
                    try {
                        FmPlayerDto dto = objectMapper.readValue(file, FmPlayerDto.class);
                        dto.setName(StringUtils.getPlayerNameFromFileName(file.getName().toUpperCase()));
                        return dto;
                    } catch (Exception e) {
                        log.error("파일 읽기 실패: {}", file.getName(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
