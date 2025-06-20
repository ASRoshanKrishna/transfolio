package com.transfolio.transfolio.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Club {
    @Id
    private String id;

    private String name;
    private String shortName;
    private String logoUrl;

    private String clubIdApi;           // Store RapidAPI clubId here
    private String competitionId;       // Store comp ID too

//    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
//    private List<UserPreference> preferences;
}
