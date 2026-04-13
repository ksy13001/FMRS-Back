package com.ksy.fmrs.controller;

import com.ksy.fmrs.service.MappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminController {

    private final MappingService mappingService;


    @PutMapping("/api/admin/mapping-4key")
    public int markUnmappedPlayers(){
        mappingService.markPlayersWithMissingMappingKeysAsFailed();
        mappingService.markDuplicates();
        int cnt = mappingService.matchExact();

        mappingService.refreshLatestFmData();
        return cnt;
    }
//
//    @PutMapping
//    public int markNoMatchPlayers(){
//        return mappingService.matchFuzzy();
//    }

}
