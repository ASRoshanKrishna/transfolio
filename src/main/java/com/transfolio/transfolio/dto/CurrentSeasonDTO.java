// CurrentSeasonDTO.java
package com.transfolio.transfolio.dto;

import lombok.Data;

import java.util.List;

@Data
public class CurrentSeasonDTO {
    private List<TransferEntryDTO> transferArrivals;
    private List<TransferEntryDTO> transferDepartures;
}
