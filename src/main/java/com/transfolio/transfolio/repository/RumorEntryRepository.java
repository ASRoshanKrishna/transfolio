package com.transfolio.transfolio.repository;

import com.transfolio.transfolio.model.RumorEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RumorEntryRepository extends JpaRepository<RumorEntry, String> {

    List<RumorEntry> findByTrackedClubIdOrderByLastPostDateDesc(String clubId);

    boolean existsById(String id);
}
