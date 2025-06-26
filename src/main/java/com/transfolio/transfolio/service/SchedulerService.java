package com.transfolio.transfolio.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final TransferFetcherService transferFetcherService;
    private final TransferNewsService rumorFetcherService; // ✅ Add this

    // 🕒 Runs every 12 hours (in milliseconds)
    @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
    public void runScheduledFetch() {
        System.out.println("📡 Scheduler: Fetching transfer and rumor updates for all users...");

        try {
            transferFetcherService.fetchTransfersForAllUsers();
            System.out.println("✅ Scheduler: Completed fetching transfers.");
        } catch (Exception e) {
            System.err.println("❌ Scheduler error (transfers): " + e.getMessage());
            e.printStackTrace();
        }

        try {
            rumorFetcherService.fetchRumorsForAllUsers();  // ✅ Fetch rumors for all users
            System.out.println("✅ Scheduler: Completed fetching rumors.");
        } catch (Exception e) {
            System.err.println("❌ Scheduler error (rumors): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
