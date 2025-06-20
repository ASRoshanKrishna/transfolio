package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.service.TransferNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferNewsController {

    @Autowired
    private TransferNewsService transferNewsService;

    @GetMapping("/rumors")
    public ResponseEntity<String> getRumors() {
        String result = transferNewsService.fetchTransferRumors();
        return ResponseEntity.ok(result);
    }
}
