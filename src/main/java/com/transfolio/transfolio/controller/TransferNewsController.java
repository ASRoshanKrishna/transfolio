package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.dto.TransferRumorDTO;
import com.transfolio.transfolio.service.TransferNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class TransferNewsController {

    @Autowired
    private TransferNewsService transferNewsService;

    @GetMapping("/rumors")
    public ResponseEntity<List<TransferRumorDTO>> getRumors(@RequestParam String clubId) {
        List<TransferRumorDTO> rumors = transferNewsService.fetchTransferRumors(clubId);
        return ResponseEntity.ok(rumors);
    }
}
