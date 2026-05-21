package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.FuzzyMappingRequestDto;
import com.ksy.fmrs.dto.FuzzyMappingResponseDto;
import com.ksy.fmrs.dto.MappingJobResponseDto;
import com.ksy.fmrs.service.MappingJobService;
import com.ksy.fmrs.service.MappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final MappingService mappingService;
    private final MappingJobService mappingJobService;


    @PutMapping("/api/admin/mapping/4key")
    public int markUnmappedPlayers() {
        mappingService.markPlayersWithMissingMappingKeysAsFailed();
        mappingService.markDuplicates();
        int cnt = mappingService.matchExact();

        mappingService.refreshLatestFmData();
        return cnt;
    }

    @PutMapping("/api/admin/mapping/4key/name-exact")
    public int matchTokenNamePlayers() {
        return mappingService.matchTokenName();
    }

    @PutMapping("/api/admin/mapping/first-name-token-first-last-name-token")
    public int matchFirstNameTokenAndFirstLastNameTokenPlayers() {
        return mappingService.matchFirstNameTokenAndFirstLastNameToken();
    }

    @PostMapping("/api/admin/mapping/jobs/fuzzy")
    public ResponseEntity<MappingJobResponseDto> startFuzzyMappingJob(
            @RequestBody FuzzyMappingRequestDto fuzzyMappingRequestDto
    ) {
        return ResponseEntity
                .accepted()
                .body(mappingJobService.startFuzzyMappingJob(fuzzyMappingRequestDto.strategy(), fuzzyMappingRequestDto.dryRun()));
    }

    @GetMapping("/api/admin/mapping/jobs/current")
    public ResponseEntity<MappingJobResponseDto> getCurrentMappingJob() {
        return mappingJobService.getCurrentMappingJob()
                .map(ResponseEntity::ok)
                .orElseGet(()->ResponseEntity.noContent().build());
    }

    @GetMapping("/api/admin/mapping/jobs/all")
    public ResponseEntity<List<MappingJobResponseDto>> getAllMappingJobs() {
        return ResponseEntity.ok(mappingJobService.getAllMappingJobs());
    }

    @GetMapping("/api/admin/mapping/jobs/{jobId}")
    public MappingJobResponseDto getMappingJob(@PathVariable String jobId) {
        return mappingJobService.getJob(jobId);
    }
}
