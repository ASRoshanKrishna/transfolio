package com.transfolio.transfolio.repository;

import com.transfolio.transfolio.model.NewsEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsEntryRepository extends JpaRepository<NewsEntry, Long> {
}
