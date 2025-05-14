package com.ksy.fmrs.dto.search;

import com.ksy.fmrs.domain.player.Player;
import com.ksy.fmrs.dto.player.PlayerDetailsDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class SearchPlayerResponseDto {

    private List<PlayerDetailsDto> players;
    private Boolean hasNext;
    private Integer totalPages;
    private Long totalElements;

    public SearchPlayerResponseDto(List<PlayerDetailsDto> players, Boolean hasNext, Integer totalPages, Long totalElements) {
        this.players = players;
        this.hasNext = hasNext;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public static SearchPlayerResponseDto  fromSlice(
            List<PlayerDetailsDto> players, Boolean hasNext
    ){
        return new SearchPlayerResponseDto(players, hasNext, null, null);
    }

    public static SearchPlayerResponseDto fromPage(
            List<PlayerDetailsDto> players, Integer totalPages
    ){
        return new SearchPlayerResponseDto(players, null, totalPages, null);
    }
}
