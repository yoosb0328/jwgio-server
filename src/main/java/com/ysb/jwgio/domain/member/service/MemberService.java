package com.ysb.jwgio.domain.member.service;

import com.ysb.jwgio.domain.match.dto.TotalMatchCountResponse;
import com.ysb.jwgio.domain.match.entity.Match;
import com.ysb.jwgio.domain.match.entity.MatchPlayer;
import com.ysb.jwgio.domain.match.repository.MatchPlayerRepository;
import com.ysb.jwgio.domain.match.repository.MatchRepository;
import com.ysb.jwgio.domain.member.dto.*;
import com.ysb.jwgio.domain.member.entity.Member;
import com.ysb.jwgio.domain.member.repository.MemberRepository;
import com.ysb.jwgio.domain.position.Positions;
import com.ysb.jwgio.global.auth.oauth2.OAuth2UserToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    @Transactional
    public ProfileUpdateDto updateProfile(Long userId, String imgUrl, String name, int number, int positionIndex) {

        Member member = memberRepository.findById(userId).get();
        if(imgUrl.isEmpty()) imgUrl = member.getProfileImg();
        member.updateProfile(imgUrl, name, number, Positions.findPosition(positionIndex));
        OAuth2UserToken authentication = (OAuth2UserToken) SecurityContextHolder.getContext().getAuthentication();
        authentication.getPrincipal().changeUsernameAttribute(name);

        return new ProfileUpdateDto(imgUrl, name, number, positionIndex);
    }

    public ProfileReadResponse readProfile(Long userId) {
        Optional<Member> memberProfileById = memberRepository.findMemberProfileById(userId);
        if(memberProfileById.isEmpty()) {
            //경기 기록이 없는 경우 위의 페치 조인으로 가져오면 NPE 발생.
            Member member = memberRepository.findById(userId).get();
            return ProfileReadResponse.builder()
                    .name(member.getUsername())
                    .positionIndex(Positions.findPosition(member.getPosition().getCode()).getIndex())
                    .number(member.getJerseyNumber())
                    .imgUrl(member.getProfileImg())
                    .totalGoal(member.getTotalGoal()).totalAssist(member.getTotalAssist()).totalWin(member.getTotalWin()).totalDraw(member.getTotalDraw()).totalLose(member.getTotalLose())
                    .left_ala_Goal(member.getLeft_ala_goal()).left_ala_Assist(member.getLeft_ala_assist()).left_ala_Win(member.getLeft_ala_win()).left_ala_Draw(member.getLeft_ala_draw()).left_ala_Lose(member.getLeft_ala_lose())
                    .right_ala_Goal(member.getRight_ala_goal()).right_ala_Assist(member.getRight_ala_assist()).right_ala_Win(member.getRight_ala_win()).right_ala_Draw(member.getRight_ala_draw()).right_ala_Lose(member.getRight_ala_lose())
                    .fixo_Goal(member.getFixo_goal()).fixo_Assist(member.getFixo_assist()).fixo_Win(member.getFixo_win()).fixo_Draw(member.getFixo_draw()).fixo_Lose(member.getFixo_lose())
                    .pivot_Goal(member.getPivot_goal()).pivot_Assist(member.getPivot_assist()).pivot_Win(member.getPivot_win()).pivot_Draw(member.getPivot_draw()).pivot_Lose(member.getPivot_lose())
                    .goleiro_Goal(member.getGoleiro_goal()).goleiro_Assist(member.getGoleiro_assist()).goleiro_cs(member.getGoleiro_cs()).goleiro_Win(member.getGoleiro_win()).goleiro_Draw(member.getGoleiro_draw()).goleiro_Lose(member.getGoleiro_lose())
                    .rate(member.getRate()).managerRate(member.getManagerRate())
                    .mvpCount(0).matchCount(0)
                    .build();
        }

        Member member = memberProfileById.get();
        Optional<List<Match>> matchByMvp = matchRepository.findMatchByMvp(userId);
        int mvpCount = 0;
        if(matchByMvp.isPresent()) mvpCount = matchByMvp.get().size();

        return ProfileReadResponse.builder()
                .name(member.getUsername())
                .positionIndex(Positions.findPosition(member.getPosition().getCode()).getIndex())
                .number(member.getJerseyNumber())
                .imgUrl(member.getProfileImg())
                .totalGoal(member.getTotalGoal()).totalAssist(member.getTotalAssist()).totalWin(member.getTotalWin()).totalDraw(member.getTotalDraw()).totalLose(member.getTotalLose())
                .left_ala_Goal(member.getLeft_ala_goal()).left_ala_Assist(member.getLeft_ala_assist()).left_ala_Win(member.getLeft_ala_win()).left_ala_Draw(member.getLeft_ala_draw()).left_ala_Lose(member.getLeft_ala_lose())
                .right_ala_Goal(member.getRight_ala_goal()).right_ala_Assist(member.getRight_ala_assist()).right_ala_Win(member.getRight_ala_win()).right_ala_Draw(member.getRight_ala_draw()).right_ala_Lose(member.getRight_ala_lose())
                .fixo_Goal(member.getFixo_goal()).fixo_Assist(member.getFixo_assist()).fixo_Win(member.getFixo_win()).fixo_Draw(member.getFixo_draw()).fixo_Lose(member.getFixo_lose())
                .pivot_Goal(member.getPivot_goal()).pivot_Assist(member.getPivot_assist()).pivot_Win(member.getPivot_win()).pivot_Draw(member.getPivot_draw()).pivot_Lose(member.getPivot_lose())
                .goleiro_Goal(member.getGoleiro_goal()).goleiro_Assist(member.getGoleiro_assist()).goleiro_cs(member.getGoleiro_cs()).goleiro_Win(member.getGoleiro_win()).goleiro_Draw(member.getGoleiro_draw()).goleiro_Lose(member.getGoleiro_lose())
                .rate(member.getRate()).managerRate(member.getManagerRate())
                .mvpCount(mvpCount).matchCount(member.getMatchCount())
                .build();
    }

    public JerseyNumberCheckResponse checkJerseyNumber(int jerseyNumber) {
        Optional<Member> memberByJerseyNumber = memberRepository.findMemberByJerseyNumber(jerseyNumber);
        if(memberByJerseyNumber.isEmpty()) {
            return JerseyNumberCheckResponse.builder().isUsed(false).build();
        } else {
            Member member = memberByJerseyNumber.get();
            return JerseyNumberCheckResponse.builder()
                    .username(member.getUsername())
                    .isUsed(true).build();
        }
    }

    public List<AllMemberReadResponse> readAllMember() {
        Optional<List<Member>> allByOrderByJerseyNumberAsc = memberRepository.findAllByOrderByJerseyNumberAsc();
        List<AllMemberReadResponse> response = new ArrayList<>();
        if(allByOrderByJerseyNumberAsc.isEmpty()) {
            response.add(AllMemberReadResponse.builder().isEmpty(true).build());
            return response;
        }
        List<Member> members = allByOrderByJerseyNumberAsc.get();
        response = members.stream()
                .map(member -> new AllMemberReadResponse(member))
                .collect(Collectors.toList());
        return response;
    }

    public List<TopPlayerRecordData> readTopGoal() {
        Optional<List<Member>> top5TotalGoalDescBy = memberRepository.findTop5ByOrderByTotalGoalDesc();
        List<TopPlayerRecordData> response = new ArrayList<>();
        if(top5TotalGoalDescBy.isEmpty()) {
            response.add(TopPlayerRecordData.builder().isEmpty(true).build());
            return response;
        }
        List<Member> members = top5TotalGoalDescBy.get();
        response = members.stream()
                .map(member -> new TopPlayerRecordData(member))
                .collect(Collectors.toList());
        return response;
    }

    public List<TopPlayerRecordData> readTopAssist() {
        Optional<List<Member>> top5TotalAssistDescBy = memberRepository.findTop5ByOrderByTotalAssistDesc();
        List<TopPlayerRecordData> response = new ArrayList<>();
        if(top5TotalAssistDescBy.isEmpty()) {
            response.add(TopPlayerRecordData.builder().isEmpty(true).build());
            return response;
        }
        List<Member> members = top5TotalAssistDescBy.get();
        response = members.stream()
                .map(member -> new TopPlayerRecordData(member))
                .collect(Collectors.toList());
        return response;
    }

    public List<TopPlayerRecordData> readTopRate() {
        Optional<List<Member>> top5RateDescBy = memberRepository.findTop5ByOrderByRateDesc();
        List<TopPlayerRecordData> response = new ArrayList<>();
        if(top5RateDescBy.isEmpty()) {
            response.add(TopPlayerRecordData.builder().isEmpty(true).build());
            return response;
        }
        List<Member> members = top5RateDescBy.get();
        response = members.stream()
                .map(member -> new TopPlayerRecordData(member))
                .collect(Collectors.toList());
        return response;
    }

    public List<TopPlayerRecordData> readTopManagerRate() {
        Optional<List<Member>> top5ManagerRateDescBy = memberRepository.findTop5ByOrderByManagerRateDesc();
        List<TopPlayerRecordData> response = new ArrayList<>();
        if(top5ManagerRateDescBy.isEmpty()) {
            response.add(TopPlayerRecordData.builder().isEmpty(true).build());
            return response;
        }
        List<Member> members = top5ManagerRateDescBy.get();
        response = members.stream()
                .map(member -> new TopPlayerRecordData(member))
                .collect(Collectors.toList());
        return response;
    }

    public List<TopPlayerRecordData> readTopMatch() {
//        Optional<List<Member>> top5CountMatchesDescBy = memberRepository.findTop5CountMatchesDescBy();
//        List<TopPlayerRecordData> response = new ArrayList<>();
//        if(top5CountMatchesDescBy.isEmpty()) {
//            response.add(TopPlayerRecordData.builder().isEmpty(true).build());
//            return response;
//        }
//        List<Member> members = top5CountMatchesDescBy.get();
//        response = members.stream()
//                .map(member -> new TopPlayerRecordData(member))
//                .collect(Collectors.toList());
        Optional<List<TotalMatchCountResponse>> top5MatchCount = matchPlayerRepository.findTop5MatchCount();
        List<TopPlayerRecordData> response = new ArrayList<>();
        if(top5MatchCount.isEmpty()) {
            response.add(TopPlayerRecordData.builder().isEmpty(true).build());
            return response;
        }
        List<TotalMatchCountResponse> totalMatchCountResponses = top5MatchCount.get();
        response = totalMatchCountResponses.stream()
                .map(count -> new TopPlayerRecordData(count))
                .collect(Collectors.toList());

        return response;
    }
}
