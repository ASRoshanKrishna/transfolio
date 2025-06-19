package com.transfolio.transfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class NewsEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String source;
    private String url;
    private String publishedAt;

    @Column(length = 3000)
    private String content;

    @Column(length = 3000)
    private String summary;

    private boolean isRelevant;

    @ManyToOne
    private Club club;

    @ManyToOne
    private Player player;
}
