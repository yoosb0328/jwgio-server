package com.ysb.jwgio.domain.match.repository;

import com.ysb.jwgio.domain.match.entity.Match;
import com.ysb.jwgio.domain.member.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Repository
public class MatchPagingRepository {

    private final EntityManager em;

    public MatchPagingRepository(EntityManager em) {
        this.em = em;
    }

    public List<Match> findAll(Long lastMatchId, int limit) {
        return em.createQuery(
                "select m from Match m where m.id < :matchId and m.status = :status order by m.date desc", Match.class
        ).setMaxResults(limit)
        .setParameter("matchId", lastMatchId)
        .setParameter("status", 4)
        .getResultList();
    }

    public List<Match> findAll(int limit) {
        return em.createQuery(
                        "select m from Match m where m.status = :status order by m.date desc", Match.class
                ).setMaxResults(limit)
                .setParameter("status", 4)
                .getResultList();
    }

    public String findMvpUsername(Long memberId) {
        return em.createQuery(
                "select m.username from Member m where m.id = :memberId", String.class
        ).setParameter("memberId", memberId)
        .getSingleResult();
    }
}
