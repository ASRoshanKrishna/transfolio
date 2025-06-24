package com.transfolio.transfolio.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rumor_entry")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RumorEntry {

    @Id
    private String id; // Same as rumor ID from API

    private String playerID;
    private String fromClubID;
    private String toClubID;
    private boolean isClosed;
    private String probability;
    private String progression;
    private String threadUrl;
    private String lang;
    private long lastPostDate;
    private String closedType;
    private long marketValue;
    private String currency;

    @Column(length = 2000)
    private String summary;

    private String trackedClubId;
}
