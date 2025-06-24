package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.model.NewsEntry;
import com.transfolio.transfolio.model.RumorEntry;
import com.transfolio.transfolio.service.PersonalizedNewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personalized")
@RequiredArgsConstructor
public class PersonalizedNewsController {

    private final PersonalizedNewsService personalizedService;

    @GetMapping("/news/{userId}")
    public List<NewsEntry> getPersonalizedNews(@PathVariable Long userId) {
        return personalizedService.getNewsForUser(userId);
    }

    @GetMapping("/rumors/{userId}")
    public List<RumorEntry> getPersonalizedRumors(@PathVariable Long userId) {
        return personalizedService.getRumorsForUser(userId);
    }
}
