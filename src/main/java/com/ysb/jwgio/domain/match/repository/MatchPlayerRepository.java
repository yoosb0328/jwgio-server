package com.ysb.jwgio.domain.match.repository;

import com.ysb.jwgio.domain.match.dto.TotalMatchCountResponse;
import com.ysb.jwgio.domain.match.entity.Match;
import com.ysb.jwgio.domain.match.entity.MatchPlayer;
import com.ysb.jwgio.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    @Query("select m from MatchPlayer m join fetch m.quarterStats where m.member = :member and m.match = :match")
    Optional<MatchPlayer> findMatchPlayerFetchJoin(@Param("member") Member member, @Param("match") Match match);

    Optional<List<MatchPlayer>> findMatchPlayerByMatch(Match match);

    Long deleteMatchPlayerByMatchAndMember(Match match, Member member);

    @Query(value = "select count(MP.MEMBER_ID) AS totalMatch, MP.MEMBER_ID AS memberId, M.USERNAME, M.jersey_number AS jerseyNumber, M.profile_img AS img, M.position " +
            "from MATCH_PLAYER MP left join MEMBER M on MP.MEMBER_ID = M.MEMBER_ID " +
            "group by MP.MEMBER_ID, M.USERNAME, M.jersey_number, M.profile_img, M.position " +
            "order by count(MP.MEMBER_ID) desc limit 5", nativeQuery = true)
    Optional<List<TotalMatchCountResponse>> findTop5MatchCount();
}
