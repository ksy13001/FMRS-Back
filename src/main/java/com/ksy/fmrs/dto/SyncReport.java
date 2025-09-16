package com.ksy.fmrs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SyncReport {
    private Integer total;
    private Integer success;
    private Integer failed;
}
