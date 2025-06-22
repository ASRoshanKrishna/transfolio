package com.transfolio.transfolio.service;

import com.transfolio.transfolio.model.User;
import com.transfolio.transfolio.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmailService emailService;
    private final UserRepository userRepo;

    public void notifyUser(Long userId, String summary) {
        User user = userRepo.findById(userId).orElse(null);

        if (user == null || user.getEmail() == null) {
            System.err.println("⚠️ Cannot notify user: user not found or email missing");
            return;
        }

        String subject = "🚨 New Transfer Update for Your Club!";
        emailService.sendEmail(user.getEmail(), subject, summary);
    }
}
