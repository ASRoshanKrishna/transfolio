package com.transfolio.transfolio.repository;

import com.transfolio.transfolio.model.RumorEntry;
import com.transfolio.transfolio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RumorEntryRepository extends JpaRepository<RumorEntry, Long> {

    List<RumorEntry> findByTrackedClubIdOrderByLastPostDateDesc(String clubId);
    boolean existsById(Long id);
    // ✅ New: Check existence per user
    boolean existsByIdAndUser(Long id, User user);
    boolean existsByOriginalRumorIdAndUser(String originalRumorId, User user);
    List<RumorEntry> findByTrackedClubIdAndUserOrderByLastPostDateDesc(String clubId, User user);
}
