package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.FuzzyMappingResponseDto;
import com.ksy.fmrs.dto.MappingJobResponseDto;
import com.ksy.fmrs.service.MappingJobService;
import com.ksy.fmrs.service.MappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final MappingService mappingService;
    private final MappingJobService mappingJobService;


    @PutMapping("/api/admin/mapping/4key")
    public int markUnmappedPlayers(){
        mappingService.markPlayersWithMissingMappingKeysAsFailed();
        mappingService.markDuplicates();
        int cnt = mappingService.matchExact();

        mappingService.refreshLatestFmData();
        return cnt;
    }


    @PutMapping("/api/admin/mapping/fuzzy")
    public FuzzyMappingResponseDto markNoMatchPlayers(){
        return mappingService.matchFuzzy();
    }

    @PostMapping("/api/admin/mapping/jobs/fuzzy")
    public ResponseEntity<MappingJobResponseDto> startFuzzyMappingJob() {
        return ResponseEntity
                .accepted()
                .body(mappingJobService.startFuzzyMappingJob());
    }

    @GetMapping("/api/admin/mapping/jobs/{jobId}")
    public MappingJobResponseDto getMappingJob(@PathVariable String jobId) {
        return mappingJobService.getJob(jobId);
    }

}
