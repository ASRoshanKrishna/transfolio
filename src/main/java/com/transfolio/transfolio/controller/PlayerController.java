package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.model.Player;
import com.transfolio.transfolio.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    @Autowired
    private PlayerRepository playerRepo;

    @PostMapping
    public Player addPlayer(@RequestBody Player player) {
        return playerRepo.save(player);
    }

    @GetMapping
    public List<Player> getAllPlayers() {
        return playerRepo.findAll();
    }
}
