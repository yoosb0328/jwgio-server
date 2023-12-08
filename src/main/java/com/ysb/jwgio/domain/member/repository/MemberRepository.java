package com.ysb.jwgio.domain.member.repository;

import com.ysb.jwgio.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberByUsername(String username);
    @Query("select m from Member m join fetch m.authorities where m.email = :email")
    Optional<Member> findMemberByEmail(@Param("email") String email);

    @Query("select m from Member m join fetch m.matches where m.id = :id")
    Optional<Member> findMemberFetchJoin(@Param("id") Long id);
    @Query("select m from Member m join fetch m.matches where m.id = :id")
    Optional<Member> findMemberProfileById(@Param("id") Long id);

    Optional<Member> findMemberByJerseyNumber(int jerseyNumber);

    Optional<List<Member>> findAllByOrderByJerseyNumberAsc();
    Optional<List<Member>> findTop5ByOrderByTotalGoalDesc();
    Optional<List<Member>> findTop5ByOrderByTotalAssistDesc();
    Optional<List<Member>> findTop5ByOrderByRateDesc();
    Optional<List<Member>> findTop5ByOrderByManagerRateDesc();
//    @Query("select m from Member m order by m.managerRate desc limit 5")
//    Optional<List<Member>> findTop5ByManagerRateOrderByDescBy();
//    @Query("select m from Member m order by m.rate desc limit 5")
//    Optional<List<Member>> findTop5OrderByRateDesc();

    //    Optional<List<Member>> findTop5RateDescBy();
//    Optional<List<Member>> findTop5ByOrderByTotalGoalDesc();
//    Optional<List<Member>> findTop5ByOrderByTotalAssistDesc();
//    Optional<List<Member>> findTop5ByOrderByManagerRateDesc();
//    Optional<List<Member>> findTop5ByOrderByRateDesc();
    Optional<List<Member>> findTop5CountMatchesDescBy();

//    Optional<List<Member>> findTop5CountMatchesDescBy();

}
