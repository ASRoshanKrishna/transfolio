package com.transfolio.transfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String position;

    @ManyToOne
    private Club club;
}
