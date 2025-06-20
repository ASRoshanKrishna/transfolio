package com.transfolio.transfolio.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<UserPreference> preferences;
}
