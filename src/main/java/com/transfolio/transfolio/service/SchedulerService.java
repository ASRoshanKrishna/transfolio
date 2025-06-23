package com.transfolio.transfolio.service;

import com.transfolio.transfolio.service.TransferFetcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final TransferFetcherService transferFetcherService;

    // 🕒 Runs every 15 minutes (in milliseconds)
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void runScheduledTransferFetch() {
        System.out.println("📡 Scheduler: Fetching transfer updates for all users...");

        try {
            transferFetcherService.fetchTransfersForAllUsers();
            System.out.println("✅ Scheduler: Completed fetching transfers.");
        } catch (Exception e) {
            System.err.println("❌ Scheduler error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
