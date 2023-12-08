package com.ysb.jwgio.domain.match.repository;

import com.ysb.jwgio.domain.match.entity.Quarter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuarterRepository extends JpaRepository<Quarter, Long> {

    Optional<Quarter> findQuarterByMatchIdAndNum(Long match_id, int num);
    Optional<List<Quarter>> findQuarterByMatchIdOrderByNumAsc(Long match_id);
}
