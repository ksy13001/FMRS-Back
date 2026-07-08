package com.ksy.fmrs.controller;

import com.ksy.fmrs.dto.ExactMappingRequestDto;
import com.ksy.fmrs.dto.FuzzyMappingRequestDto;
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


//    @PutMapping("/api/admin/mapping/4key")
//    public int markUnmappedPlayers() {
//        mappingService.markPlayersWithMissingMappingKeysAsFailed();
//        mappingService.markDuplicates();
//        int cnt = mappingService.matchExact();
//
//        mappingService.markRemainingPlayersAsNoMatch();
//        mappingService.refreshLatestFmData();
//        return cnt;
//    }

    @PostMapping("/api/admin/mapping/jobs/4key")
    public ResponseEntity<MappingJobResponseDto> startExact4KeyMappingJob(
            @RequestBody ExactMappingRequestDto exactMappingRequestDto
    ) {
        return ResponseEntity
                .accepted()
                .body(mappingJobService.startExact4KeyMappingJob(exactMappingRequestDto.dryRun()));
    }

    @PostMapping("/api/admin/mapping/jobs/4key/name-exact")
    public ResponseEntity<MappingJobResponseDto> startTokenNameExactMappingJob(
            @RequestBody ExactMappingRequestDto exactMappingRequestDto
    ) {
        return ResponseEntity
                .accepted()
                .body(mappingJobService.startTokenNameExactMappingJob(exactMappingRequestDto.dryRun()));
    }

    @PostMapping("/api/admin/mapping/jobs/first-name-token-first-last-name-token")
    public ResponseEntity<MappingJobResponseDto> startFirstNameTokenAndFirstLastNameTokenExactMappingJob(
            @RequestBody ExactMappingRequestDto exactMappingRequestDto
    ) {
        return ResponseEntity
                .accepted()
                .body(mappingJobService.startFirstNameTokenAndFirstLastNameTokenExactMappingJob(
                        exactMappingRequestDto.dryRun()
                ));
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
