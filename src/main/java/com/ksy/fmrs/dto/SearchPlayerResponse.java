package com.ksy.fmrs.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class SearchPlayerResponse {

    private final List<PlayerDetailsResponse> players;
}
