package com.ksy.fmrs.domain.player;


import com.ksy.fmrs.domain.Team;
import com.ksy.fmrs.domain.enums.PlayerMappingStatus;
import com.ksy.fmrs.util.TimeUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_api_id", unique = true)
    private Integer playerApiId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private LocalDate birth;

    private int height;

    private int weight;

    @Column(name = "nation_name")
    private String nationName;

    @Column(name = "nation_logo_url")
    private String nationLogoUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name="mapping_status")
    private PlayerMappingStatus mappingStatus;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToOne
    @JoinColumn(name = "fmplayer_id", unique = true)
    private FmPlayer fmPlayer;

    @Builder
    public Player(
            Integer playerApiId,
            String firstName,
            String lastName,
            LocalDate birth,
            int height,
            int weight,
            String imageUrl,
            String nationName,
            String nationLogoUrl,
            PlayerMappingStatus mappingStatus
    ) {
        this.playerApiId = playerApiId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.imageUrl = imageUrl;
        this.nationName = nationName;
        this.nationLogoUrl = nationLogoUrl;
        this.mappingStatus = mappingStatus;
    }

    // 연관관계 설정 메서드
    public void updateTeam(Team team) {
        this.team = team;
        team.getPlayers().add(this);
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void updateFmPlayer(FmPlayer fmPlayer) {
        this.fmPlayer = fmPlayer;
    }

    private void updateStatus(PlayerMappingStatus status) {
        this.mappingStatus = status;
    }

    public int getAge(){
        return TimeUtils.getAge(this.birth);
    }

    public void updatePlayerApiId(Integer playerApiId) {
        this.playerApiId = playerApiId;
    }


    public String getStringBirth(){
        return String.valueOf(birth);
    }
}
