package com.transfolio.transfolio.dto;// package: dto
import lombok.Data;

@Data
public class TransferRumorDTO {
    private String id;
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
    private long marketValue;  // flat value
    private String currency;
}
