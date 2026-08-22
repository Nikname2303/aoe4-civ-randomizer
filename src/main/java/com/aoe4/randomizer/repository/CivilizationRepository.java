package com.aoe4.randomizer.repository;

import com.aoe4.randomizer.model.Civilization;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CivilizationRepository extends JpaRepository<Civilization, Long> {
    List<Civilization> findAllByOrderByDlcAscNameAsc();
    List<Civilization> findByEnabledTrue();
    List<Civilization> findByDlc(String dlc);
}
