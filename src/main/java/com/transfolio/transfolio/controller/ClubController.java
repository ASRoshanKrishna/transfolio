package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.model.Club;
import com.transfolio.transfolio.repository.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    @Autowired
    private ClubRepository clubRepo;

    @PostMapping
    public Club addClub(@RequestBody Club club) {
        return clubRepo.save(club);
    }

    @GetMapping
    public List<Club> getAllClubs() {
        return clubRepo.findAll();
    }
}
