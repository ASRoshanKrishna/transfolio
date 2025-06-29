package com.transfolio.transfolio.controller;

import com.transfolio.transfolio.dto.SearchResultDTO;
import com.transfolio.transfolio.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/clubs")
    public List<SearchResultDTO> searchClubs(@RequestParam String query,
                                             @RequestParam(required = false) Long userId) {
        System.out.println("query is: " + query + " from userId: " + userId);
        return searchService.searchClubs(query);
    }
}
