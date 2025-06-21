package com.transfolio.transfolio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore  // 💡 prevent infinite recursion
    private User user;

    private String clubIdApi;         // e.g. "131"
    private String clubName;          // e.g. "FC Barcelona"
    private String competitionId;     // e.g. "ES1"
    private String competitionName;   // e.g. "LaLiga"
    private String logoUrl;           // club logo
}
