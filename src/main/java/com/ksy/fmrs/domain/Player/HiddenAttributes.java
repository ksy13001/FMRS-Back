package com.ksy.fmrs.domain.Player;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class HiddenAttributes {

    private int consistency;
    private int dirtiness;
    @Column(name = "important_matches")
    private int importantMatches;
    @Column(name = "injury_proneness")
    private int injuryProneness;
    private int versatility;
}

