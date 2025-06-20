package com.transfolio.transfolio.dto;

import lombok.Data;

@Data
public class UserPreferenceDTO {
    private Long userId;
    private String clubIdApi;
    private String clubName;
    private String competitionId;
    private String competitionName;
    private String logoUrl;
}
