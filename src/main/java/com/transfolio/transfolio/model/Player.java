package com.transfolio.transfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Player {
    @Id
    @Column(columnDefinition = "varchar(255)") // 👈 force DB to treat it as VARCHAR
    private String id;
    private String name;
    private String position;

    @ManyToOne
    private Club club;
}
