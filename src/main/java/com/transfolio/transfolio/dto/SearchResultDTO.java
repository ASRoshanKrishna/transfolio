package com.transfolio.transfolio.dto;

import lombok.Data;

@Data
public class SearchResultDTO {
    private String id;                 // clubId from API
    private String name;              // Club name
    private String logoUrl;           // Club logo image
    private String competitionId;     // For API filter
    private String competitionName;   // Display nicely in frontend
}
