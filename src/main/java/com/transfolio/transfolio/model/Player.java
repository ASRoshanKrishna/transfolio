package com.transfolio.transfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Player {
    @Id
    private String id;
    private String name;
    private String position;

    @ManyToOne
    private Club club;
}
