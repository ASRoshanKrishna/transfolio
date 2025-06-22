package com.transfolio.transfolio.repository;

import com.transfolio.transfolio.model.NewsEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NewsEntryRepository extends JpaRepository<NewsEntry, Long> {

    // Fetch all entries for a specific club by its internal DB ID
    List<NewsEntry> findByClub_Id(String clubId);

    // ✅ Used to check if this transfer already exists before re-saving
    boolean existsByPlayer_IdAndTransferDateAndClub_Id(String playerId, LocalDate transferDate, String clubId);
    List<NewsEntry> findByClub_IdOrderByTransferDateDesc(String clubId);
}
