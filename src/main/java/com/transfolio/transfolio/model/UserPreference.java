package com.transfolio.transfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    private Boolean notificationsEnabled = true;
}
