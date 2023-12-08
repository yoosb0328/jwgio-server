package com.ysb.jwgio.domain.match.service;

import com.ysb.jwgio.domain.match.dto.*;
import com.ysb.jwgio.domain.match.entity.*;
import com.ysb.jwgio.domain.match.repository.MatchPagingRepository;
import com.ysb.jwgio.domain.match.repository.MatchPlayerRepository;
import com.ysb.jwgio.domain.match.repository.MatchRepository;
import com.ysb.jwgio.domain.match.repository.QuarterRepository;
import com.ysb.jwgio.domain.member.dto.TopPlayerRecordData;
import com.ysb.jwgio.domain.member.entity.Member;
import com.ysb.jwgio.domain.member.repository.MemberRepository;
import com.ysb.jwgio.global.auth.oauth2.OAuth2UserToken;
import com.ysb.jwgio.global.common.converter.LocalDateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MatchService {

    private final MatchRepository matchRepository;
    private final QuarterRepository quarterRepository;
    private final MemberRepository memberRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final MatchPagingRepository matchPagingRepository;
    private final LocalDateTimeConverter localDateTimeConverter;

    @Transactional
    public MatchRegisterResponse registerMatch(MatchRegisterRequest request, Long member_id) {
        //매치를 등록하기 전, 이미 등록한 대기중인 매치(상태 0, 1, 2)가 있는지 확인합니다.
        ArrayList<Integer> status = new ArrayList<>();
        status.add(0);
        status.add(1);
        status.add(2);
        if(matchRepository.findMatchByStatusIsIn(status).isPresent()) {
            return MatchRegisterResponse.builder()
                    .isExist(true).build();
        }

        LocalDateTime date = localDateTimeConverter.ISOStringToLocalDateTime(request.getDate());
        String stadiumCode = request.getStadiumCode();
        String link = request.getLink();
        int num = request.getNum();
        //현재 사용자 가져오기
        Member member = memberRepository.findById(member_id).get();
        //지금 등록할 매치
        Match match = Match.registerNewMatch(date, stadiumCode, link, num);
        //해당 매치에 참여 선수 정보
        MatchPlayer matchPlayer = MatchPlayer.createMatchPlayer(member, match);

//        //지금 등록한 매치에 참가자 등록
//        match.addMatchPlayer(matchPlayer);
//        //등록한 사용자 매치에 참가 처리.
//        member.joinMatch(matchPlayer);

        Match saved = matchRepository.save(match);
        memberRepository.save(member);

        // 업데이트 확인
        // 사용자 정보에서 참가한 경기 가져오기.
//        List<MatchPlayer> matches = memberRepository.findMemberFetchJoin(member_id).get().getMatches();
//        MatchPlayer matchPlayer1 = matches.get(0);
//        Match match1 = matchPlayer1.getMatch();
//        log.info(match1.getId().toString());
//        // 경기 정보에서 참여자 가져오기
//        Match testFind = matchRepository.findMatchFetchJoin(match.getId()).get();
//        List<MatchPlayer> players1 = testFind.getPlayers();
//        //0번 참여자의 쿼터별 기록 가져오기
////        List<QuarterStats> quarterStats = players.get(0).getQuarterStats();
//        /////////////////
////        matchRepository.deleteById(match1.getId());
        List<String> players = new ArrayList<>();
        players.add(member.getUsername());
        List<Long> playersId = new ArrayList<>();
        playersId.add(member_id);
        return MatchRegisterResponse.builder()
                .match_id(saved.getId())
                .link(saved.getLink())
                .stadiumIndex(saved.getStadium().getIndex())
                .date(saved.getDate())
                .status(saved.getStatus())
                .num(saved.getNum())
                .players(players)
                .playersId(playersId)
                .createdBy(saved.getCreatedBy())
                .build();
    }
    @Transactional
    public MatchJoinResponse joinMatch(Long match_id, Long member_id) {

        Optional<Match> matchFetchJoin = matchRepository.findMatchFetchJoin(match_id);
        if(matchFetchJoin.isEmpty()) {
            //취소된 매치인 경우.
            return MatchJoinResponse.builder().isCanceled(true).build();
        }
        Match match = matchFetchJoin.get();
        if(match.getPlayers().size() == match.getNum()) {
            //이미 신청자가 꽉찼다면??
            return MatchJoinResponse.builder().isFull(true).build();
        }
        Member member = memberRepository.findById(member_id).get();

        MatchPlayer matchPlayer = MatchPlayer.createMatchPlayer(member, match);
//        //지금 등록한 매치에 참가자 등록
//        match.addMatchPlayer(matchPlayer);
//        //등록한 사용자 매치에 참가 처리.
//        member.joinMatch(matchPlayer);
        match.checkStatus();

        return MatchJoinResponse.builder()
                .member_id(member_id)
                .username(member.getUsername()).build();
    }

    @Transactional
    public void deleteMatch(Long match_id) {

        matchRepository.deleteById(match_id);
    }

    @Transactional
    public CurrentMatchResponse getCurrentMatch() {
        List<Long> playersId = new ArrayList<>();
        List<String> players = new ArrayList<>();

        Optional<Match> currentMatchFetchJoin = matchRepository.findCurrentMatchFetchJoin();
        if(currentMatchFetchJoin.isEmpty()) {
            return CurrentMatchResponse.builder()
                    .isEmpty(true).build();
        }

        Match match = currentMatchFetchJoin.get();
        List<MatchPlayer> matchPlayers = match.getPlayers();

        match.checkStatus();

        for(MatchPlayer matchPlayer : matchPlayers) {
            Member member = matchPlayer.getMember();
            players.add(member.getUsername());
            playersId.add(member.getId());
        }
        return CurrentMatchResponse.builder()
                .match_id(match.getId())
                .link(match.getLink())
                .stadiumIndex(match.getStadium().getIndex())
                .date(match.getDate())
                .status(match.getStatus())
                .num(match.getNum())
                .players(players)
                .playersId(playersId)
                .createdBy(match.getCreatedBy())
                .build();
    }


    public MatchRegisterResponse currentMatchExistCheck() {
        //매치를 등록하기 전, 이미 등록한 대기중인 매치(상태 0, 1, 2)가 있는지 확인합니다.
        ArrayList<Integer> status = new ArrayList<>();
        status.add(0);
        status.add(1);
        status.add(2);
        if(matchRepository.findMatchByStatusIsIn(status).isPresent()) {
            return MatchRegisterResponse.builder()
                    .isExist(true).build();
        } else {
            return MatchRegisterResponse.builder()
                    .isExist(false).build();
        }
    }

    @Transactional
    public CancelJoinMatchResponse cancelJoinMatch(Long match_id, Long member_id) {
        //신청 취소하기 전에 해당 매치가 존재하는지 확인
        Optional<Match> matchFetchJoin = matchRepository.findMatchFetchJoin(match_id);
        if(matchFetchJoin.isEmpty()) {
            return CancelJoinMatchResponse.builder().isExist(false).build();
        }
        Match match = matchFetchJoin.get();
        match.checkStatus(); //상태 최신화
        if(match.getStatus() == 3) {
            //진행중인 상태라면,
            return CancelJoinMatchResponse.builder().isPlaying(true).build();
        }

        List<MatchPlayer> matchPlayers = match.getPlayers();
        matchPlayers.stream().filter(matchPlayer -> matchPlayer.getMember().getId() == member_id)
                .collect(Collectors.toList())
                .forEach(li -> matchPlayers.remove(li));
        Member member = memberRepository.findById(member_id).get();
        Long deleteNum = matchPlayerRepository.deleteMatchPlayerByMatchAndMember(match, member);

        //삭제 후 다시 갱신해서 가져옴.
//        matchFetchJoin = matchRepository.findMatchFetchJoin(match_id);
//        match = matchFetchJoin.get();
//
//        List<MatchPlayer> matchPlayers = match.getPlayers();
        //인원 변동으로 인해 매치 상태가 변화할 수 있으므로 체크
        match.checkStatus();

        List<Long> playersId = new ArrayList<>();
        List<String> players = new ArrayList<>();
        //남은 선수명단 클라이언트에 전달
        for(MatchPlayer matchPlayer : matchPlayers) {
            players.add(matchPlayer.getMember().getUsername());
            playersId.add(matchPlayer.getMember().getId());
        }

        return CancelJoinMatchResponse.builder()
                .players(players)
                .isExist(true)
                .playersId(playersId).build();
    }

    public QuarterResultDto getQuarterResult(Long match_id, int quarterNum) {
        Optional<Quarter> quarterByMatchIdAndNum = quarterRepository.findQuarterByMatchIdAndNum(match_id, quarterNum);
        Quarter quarter = quarterByMatchIdAndNum.get();
        if(quarter.getResult() == -2) {
            return QuarterResultDto.builder()
                    .ga(quarter.getGa())
                    .gf(quarter.getGf())
                    .result(quarter.getResult())
                    .num(quarter.getNum())
                    .isUpdated(false).build();
        } else {
            return QuarterResultDto.builder()
                    .ga(quarter.getGa())
                    .gf(quarter.getGf())
                    .result(quarter.getResult())
                    .num(quarter.getNum())
                    .isUpdated(true).build();
        }
    }

    public List<QuarterResultDto> getQuarterResult(Long match_id) {
        //해당 매치의 쿼터별 스코어 반환
        Optional<List<Quarter>> quarterByMatchIdAndNum = quarterRepository.findQuarterByMatchIdOrderByNumAsc(match_id);
        List<Quarter> quarters = quarterByMatchIdAndNum.get();
        List<QuarterResultDto> list = new ArrayList<>();

        for(Quarter quarter : quarters) {
            if(quarter.getResult() == -2) {
                list.add(QuarterResultDto.builder()
                        .ga(quarter.getGa())
                        .gf(quarter.getGf())
                        .result(quarter.getResult())
                        .num(quarter.getNum())
                        .isUpdated(false).build());
            } else {
                list.add(QuarterResultDto.builder()
                        .ga(quarter.getGa())
                        .gf(quarter.getGf())
                        .result(quarter.getResult())
                        .num(quarter.getNum())
                        .isUpdated(true).build());
            }
        }
        return list;
    }

    public List<QuarterStatsDto> getQuarterStats(Long match_id, Long member_id) {
        Member member = memberRepository.findById(member_id).get();
        Match match = matchRepository.findById(match_id).get();
        //해당 매치의 선수 쿼터별 스탯 반환.
        MatchPlayer matchPlayer = matchPlayerRepository.findMatchPlayerFetchJoin(member, match).get();
        List<QuarterStats> quarterStats = matchPlayer.getQuarterStats();
        List<QuarterStatsDto> list = new ArrayList<>();
        for(QuarterStats stats : quarterStats) {
            if(stats.getPositions() == null) {
                list.add(QuarterStatsDto.builder()
                        .quarterNum(stats.getQuarter_num())
                        .goal(stats.getGoal())
                        .positionIndex(5)
                        .assist(stats.getAssist())
                        .isUpdated(false).build());
            } else {
                list.add(QuarterStatsDto.builder()
                        .quarterNum(stats.getQuarter_num())
                        .goal(stats.getGoal())
                        .assist(stats.getAssist())
                        .positionIndex(stats.getPositions().getIndex())
                        .isUpdated(true).build());
            }
        }
        return list;
    }

    public List<PlayerRatingDto> getMatchRatings(Long match_id) {
        //해당 매치의 선수 쿼터별 평점 반환.
        Match match = matchRepository.findById(match_id).get();
        List<MatchPlayer> matchPlayers = matchPlayerRepository.findMatchPlayerByMatch(match).get();
        List<PlayerRatingDto> list = new ArrayList<>();
        OAuth2UserToken authentication = (OAuth2UserToken) SecurityContextHolder.getContext().getAuthentication();
        Long member_id = Long.parseLong(authentication.getName());
//        //현재 매치의 참가자들
//        for(MatchPlayer player : matchPlayers) {
//            if(player.getMember().getId() == member_id) continue;
//
//            if(player.getRateSum() == -1.0f) {
//                list.add(PlayerRatingDto.builder()
//                        .member_id(player.getMember().getId())
//                        .username(player.getMember().getUsername())
//                        .isUpdated(false)
//                        .build());
//            } else {
//                list.add(PlayerRatingDto.builder()
//                        .member_id(player.getMember().getId())
//                        .username(player.getMember().getUsername())
//                        .isUpdated(true)
//                        .build());
//            }
//        }
        HashMap<Long, Boolean> checkMap = new HashMap<>(); //본인을 제외한 나머지 참가자들이 들어있는 HashMap
        for(MatchPlayer player : matchPlayers) {
            if(player.getMember().getId() == member_id) continue;
            checkMap.put(player.getMember().getId(), false);
        }
        //현재 매치의 참가자들
        for(MatchPlayer player : matchPlayers) {
            if(player.getMember().getId() == member_id) { // 사용자 본인
                List<MatchRateCheck> receivers = player.getReceivers(); //본인이 평가한 참가자들을 찾는다.
                if(!receivers.isEmpty()) {
                    for(MatchRateCheck receiver : receivers) {
                        Long receiverId = receiver.getReceiver_id();
                        checkMap.put(receiverId, true); //평가한 참가자는 값이 true;
                    }
                }
            }
        }

        checkMap.forEach((key, value) -> {
            Member member = memberRepository.findById(key).get();
            if(value == true) { //평가한 참가자
                list.add(PlayerRatingDto.builder()
                        .member_id(member.getId())
                        .username(member.getUsername())
                        .isUpdated(true).build());
            } else { //평가하지 않은 참가자
                list.add(PlayerRatingDto.builder()
                        .member_id(member.getId())
                        .username(member.getUsername())
                        .isUpdated(false).build());
            }
        });

        return list;
    }

    @Transactional
    public QuarterResultDto setQuarterResult(Long match_id, int quarterNum, int gf, int ga) {
        Quarter quarter = quarterRepository.findQuarterByMatchIdAndNum(match_id, quarterNum).get();
        quarter.setQuarterResult(gf, ga);
        Match match = matchRepository.findMatchFetchJoin(match_id).get();
        List<MatchPlayer> players = match.getPlayers();
        //통산 승/무/패 등록
        for(MatchPlayer player : players) {
            player.getMember().addTotalQuarterResult(quarter.getResult());
        }

        return QuarterResultDto.builder()
                .num(quarter.getNum())
                .gf(quarter.getGf())
                .ga(quarter.getGa())
                .result(quarter.getResult())
                .isUpdated(true).build();

    }

    @Transactional
    public QuarterStatsDto setQuarterStats(Long match_id, int quarterNum, int goal, int assist, int positionIndex) {
        OAuth2UserToken authentication = (OAuth2UserToken) SecurityContextHolder.getContext().getAuthentication();
        Long member_id = Long.parseLong(authentication.getName());

        Member member = memberRepository.findById(member_id).get();
        Match match = matchRepository.findById(match_id).get();
        //해당 매치의 선수 쿼터별 스탯 반환.
        MatchPlayer matchPlayer = matchPlayerRepository.findMatchPlayerFetchJoin(member, match).get();
        List<QuarterStats> quarterStats = matchPlayer.getQuarterStats();

        quarterStats.stream().forEach(stats ->{
            if(stats.getQuarter_num() == quarterNum) {
                stats.setStats(goal, assist, positionIndex);
            }
        });

        return QuarterStatsDto.builder()
                .goal(goal)
                .assist(assist)
                .quarterNum(quarterNum)
                .positionIndex(positionIndex)
                .isUpdated(true).build();
    }

    @Transactional
    public PlayerRatingDto setPlayerRating(Long match_id, Long member_id, float rate) {
        Member member = memberRepository.findById(member_id).get();
        Match match = matchRepository.findById(match_id).get();
        //해당 매치의 선수 쿼터별 스탯 반환.
        MatchPlayer matchPlayer = matchPlayerRepository.findMatchPlayerFetchJoin(member, match).get();
        OAuth2UserToken authentication = (OAuth2UserToken) SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getPrincipal().getAuthorities();
        String auth = authorities.stream().findFirst().get().toString();



        if(auth.equals("ROLE_MANAGER")) {
            matchPlayer.setManagerRate(rate);
            matchPlayer.addRateSum(rate);
        } else {
            matchPlayer.addRateSum(rate);
        }
        return PlayerRatingDto.builder()
                .rate(rate)
                .member_id(member_id)
                .username(member.getUsername())
                .isUpdated(true).build();
    }

    @Transactional
    public void setPlayerRatingCheck(Long match_id, Long member_id) { //member_id는 현재 사용자가 평점을 업데이트한 멤버
        OAuth2UserToken authentication = (OAuth2UserToken) SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = Long.parseLong(authentication.getName());
        Member member = memberRepository.findById(currentUserId).get(); //현재 평점을 업데이트한 사용자
        Match match = matchRepository.findById(match_id).get();

        MatchPlayer matchPlayer = matchPlayerRepository.findMatchPlayerFetchJoin(member, match).get();
        MatchRateCheck matchRateCheck = new MatchRateCheck(member_id);
        matchPlayer.getReceivers().add(matchRateCheck);
        matchRateCheck.setMatchPlayer(matchPlayer);
    }

    @Transactional
    public MatchCompleteResponse completeMatch() {
        // status가 3인 매치의 기록 여부를 검사합니다.
        Match match = matchRepository.findMatchFetchJoin(3).get();
        List<Quarter> quarters = quarterRepository.findQuarterByMatchIdOrderByNumAsc(match.getId()).get();
        List<MatchPlayer> players = match.getPlayers();

        //1. 쿼터 승/패 미입력 여부 검사
        for(Quarter quarter : quarters) {
            if(quarter.getResult() == -2) {
                return MatchCompleteResponse.builder().isComplete(false).build();
            }
        }

        for(MatchPlayer player : players) {
            //2. 팀원 평점 미입력 여부 검사 : 인원
            if(player.getReceivers().size() < match.getPlayers().size() - 1) {
                return MatchCompleteResponse.builder().isComplete(false).build();
            }
            //3. 경기 스탯 미입력 여부 검사
            List<QuarterStats> quarterStats = player.getQuarterStats();
            for(QuarterStats stats : quarterStats) {
                if(stats.getPositions() == null) {
                    return MatchCompleteResponse.builder().isComplete(false).build();
                }
            }
        }

        //검사를 모두 통과 => match status 4로 변경
        match.setStatus(4);

        // 매치 평점 평균 계산 및 MVP 선정.
        long mvp_id = 0l;
        float high_rate = -1f;
        float high_manager_rate = -1f;

        for(MatchPlayer player : players) {
            player.calcAvgRate();
            //MVP는 팀원 평점과 감독 평점의 평균으로 한다.(단 감독 평점이 경기에 참여하지 않아 감독 평점이 0점인 경우에는 팀원 평점만으로 판단한다)
            float rate = player.getManagerRate() == 0 ? player.getRate() : (player.getRate() + player.getManagerRate())/2f;
            if(rate > high_rate) {
                high_rate = rate;
                high_manager_rate = player.getManagerRate();
                mvp_id = player.getMember().getId();
            } else if(rate == high_rate) {
                //MVP는 팀원 평점과 감독 평점의 평균이 같다면 감독 평점이 더 높은쪽이 MVP이다.
                if(player.getManagerRate() > high_manager_rate) {
                    high_rate = rate;
                    high_manager_rate = player.getManagerRate();
                    mvp_id = player.getMember().getId();
                }
            }
            //매치 참여 선수 통산 평균 평점 업데이트
            player.getMember().calcRate();

            //해당 매치 참가자의 메인 포지션 (가장 많이 뛴) 설정
            List<QuarterStats> quarterStats = player.getQuarterStats();
            int[] arr = new int[5];
            int max = Integer.MIN_VALUE;
            int mainPostionIndex = 0;
            //해당 매치에서 쿼터별 뛴 포지션 카운팅 + 포지션별 승/무/패 등록
            for(QuarterStats stats : quarterStats) {
                //카운팅
                arr[stats.getPositions().getIndex()]++;
                //포지션별 승/무/패
                Quarter quarter = quarterRepository.findQuarterByMatchIdAndNum(match.getId(), stats.getQuarter_num()).get();
                player.getMember().addPositionQuarterResult(stats.getPositions(), quarter.getResult(), quarter.getGa());
            }
            //가장 큰 포지션 인덱스 찾기
            for(int i=0; i<5; i++) {
                if(arr[i] > max) {
                    max = arr[i];
                    mainPostionIndex = i;
                }
            }
            //메인 포지션 등록.
            player.setMainPosition(mainPostionIndex);

        }
        //MVP 등록
        match.setMvp(mvp_id);

        return MatchCompleteResponse.builder().isComplete(true).build();
    }

    public List<MatchPagingData> getMatchList(Long lastMatchId) {
        List<Match> matches = matchPagingRepository.findAll(lastMatchId, 5);
        List<MatchPagingData> response = matches.stream()
                .map(m -> new MatchPagingData(m, matchPagingRepository.findMvpUsername(m.getMvp())))
                .collect(Collectors.toList());
        return response;
    }
    public List<MatchPagingData> getMatchList() {
        List<Match> matches = matchPagingRepository.findAll(5);
        List<MatchPagingData> response = matches.stream()
                .map(m -> new MatchPagingData(m, matchPagingRepository.findMvpUsername(m.getMvp())))
                .collect(Collectors.toList());
        return response;
    }

}
