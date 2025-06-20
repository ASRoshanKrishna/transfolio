package com.transfolio.transfolio.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class NewsEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    private String playerName;

    private int age;

    private String position;

    private String transferType; // "arrival" or "departure"

    private String transferFee;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    private String clubName; // new club if arrival, old club if departure

    private String playerImage;

    private String clubImage;

    private LocalDate transferDate;

    private String countryImage;

    private String loan;

    private String positionsDetail;

    private boolean isRelevant;
}
