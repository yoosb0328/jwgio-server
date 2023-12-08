package com.ysb.jwgio.domain.match.repository;

import com.ysb.jwgio.domain.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Long> {


    @Query("select m from Match m join fetch m.players where m.id = :match_id")
    Optional<Match> findMatchFetchJoin(@Param("match_id") Long match_id);

    @Query("select m from Match m join fetch m.players where m.status = :status")
    Optional<Match> findMatchFetchJoin(@Param("status") int status);
    @Query("select m from Match m join fetch m.players where m.status in (0, 1, 2, 3)")
    Optional<Match> findCurrentMatchFetchJoin();

    Optional<Match> findMatchByStatusIsIn(List<Integer> status);
    Optional<Match> findMatchByStatusIn(int[] status);
    Optional<List<Match>> findMatchByMvp(Long mvp);


}
