package com.transfolio.transfolio.service;

import org.springframework.stereotype.Service;
@Service
public class NotificationService {

    public void notifyUser(Long userId, String message) {
        // For now: just log it or save to DB table
        System.out.println("📣 Notify User [" + userId + "]: " + message);
    }
}