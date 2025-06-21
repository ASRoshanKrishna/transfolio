package com.transfolio.transfolio.repository;

import com.transfolio.transfolio.model.NewsEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsEntryRepository extends JpaRepository<NewsEntry, Long> {

    // 🆕 Add this line
    List<NewsEntry> findByClub_Id(String clubId);
    List<NewsEntry> findByClub_ClubIdApi(String clubIdApi);
}
