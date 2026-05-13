package com.ksy.fmrs.dto;

import com.ksy.fmrs.domain.player.FmPlayer;

public record ScoredCandidate(FmPlayer fmplayer, double score){
}
