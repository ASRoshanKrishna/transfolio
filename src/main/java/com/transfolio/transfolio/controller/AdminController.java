package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.service.TransferFetcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final TransferFetcherService transferFetcherService;

    @GetMapping("/fetch-transfers")
    public String fetchTransfers() {
        transferFetcherService.fetchTransfersForAllUsers();
        return "Transfer fetch triggered ✅";
    }
}
