package com.ysb.jwgio.domain.match;

import com.ysb.jwgio.domain.match.dto.*;
import com.ysb.jwgio.domain.match.service.MatchService;
import com.ysb.jwgio.global.common.sns.SnsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final SnsService snsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody MatchRegisterRequest request)
    {
        log.info("MatchController - MatchRegisterRequest = {}", request.toString());
        //매치를 등록한다.
        //등록한 사용자를 해당 매치의 MatchPlayer에 등록한다.
        //MatchPlayer의 QuaterStats 등록한다.
        //해당 매치의 Quarter들을 등록한다.
        SecurityContext context = SecurityContextHolder.getContext();
        Long member_id = Long.parseLong(context.getAuthentication().getName());
        MatchRegisterResponse response = matchService.registerMatch(request, member_id);
        if(!response.isExist()) {
            //exist값이 false인 경우가 새로운 Match를 register한 경우입니다.

        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/currentMatch")
    public ResponseEntity<?> getCurrentMatch() {
        //현재 등록된 매치를 가져옵니다. (상태가 0 대기중 /  1 : 확정 / 2 : 진행중
        return ResponseEntity.ok(matchService.getCurrentMatch());
    }

    @DeleteMapping("/cancel/{matchId}")
    public ResponseEntity<?> cancelMatch(@PathVariable Long matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PutMapping("/join/{matchId}/{memberId}")
    public ResponseEntity<?> joinMatch(@PathVariable Long matchId, @PathVariable Long memberId) {
        return ResponseEntity.ok(matchService.joinMatch(matchId, memberId));
    }

    @PostMapping("/checkExist")
    public ResponseEntity<?> checkExist() {
        return ResponseEntity.ok(matchService.currentMatchExistCheck());
    }

    @PutMapping("/cancelJoin/{matchId}/{memberId}")
    public ResponseEntity<?> cancelJoinMatch(@PathVariable Long matchId, @PathVariable Long memberId) {
        return ResponseEntity.ok(matchService.cancelJoinMatch(matchId, memberId));
    }

    @GetMapping("/readQuarterResult/{matchId}/{quarterNum}")
    public ResponseEntity<?> readQuarterResult(@PathVariable Long matchId, @PathVariable int quarterNum) {

        return ResponseEntity.ok(matchService.getQuarterResult(matchId, quarterNum));
    }
    @GetMapping("/readResultWrite/{matchId}/{memberId}")
    public ResponseEntity<?> readResultWrite(@PathVariable Long matchId, @PathVariable Long memberId) {
        List<PlayerRatingDto> matchRatings = matchService.getMatchRatings(matchId);
        List<QuarterResultDto> quarterResult = matchService.getQuarterResult(matchId);
        List<QuarterStatsDto> quarterStats = matchService.getQuarterStats(matchId, memberId);
        ResultWriteResponse response = ResultWriteResponse.builder()
                .matchRatings(matchRatings)
                .quarterResults(quarterResult)
                .quarterStats(quarterStats).build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/writeQuarterResult/{matchId}/{quarterNum}/{gf}/{ga}")
    public ResponseEntity<?> writeQuarterResult(@PathVariable Long matchId, @PathVariable int quarterNum, @PathVariable int gf, @PathVariable int ga) {
        QuarterResultDto quarterResultDto = matchService.setQuarterResult(matchId, quarterNum, gf, ga);
        return ResponseEntity.ok(quarterResultDto);
    }

    @PutMapping("/writeQuarterStats/{matchId}/{quarterNum}/{goal}/{assist}/{positionIndex}")
    public ResponseEntity<?> writeQuarterStats(@PathVariable Long matchId, @PathVariable int quarterNum,
                                               @PathVariable int goal, @PathVariable int assist, @PathVariable int positionIndex) {
        QuarterStatsDto quarterStatsDto = matchService.setQuarterStats(matchId, quarterNum, goal, assist, positionIndex);
        return ResponseEntity.ok(quarterStatsDto);
    }

    @PutMapping("/writePlayerRating/{matchId}/{memberId}/{rate}")
    public ResponseEntity<?> writePlayerRating(@PathVariable Long matchId, @PathVariable Long memberId, @PathVariable float rate) {
        PlayerRatingDto playerRatingDto = matchService.setPlayerRating(matchId, memberId, rate);
        matchService.setPlayerRatingCheck(matchId, memberId);
        return ResponseEntity.ok(playerRatingDto);
    }

    @PutMapping("/completeMatch")
    public ResponseEntity<?> completeMatch() {
        MatchCompleteResponse matchCompleteResponse = matchService.completeMatch();
        return ResponseEntity.ok(matchCompleteResponse);
    }

    @GetMapping("/read/first")
    public ResponseEntity<?> readMatchesFirst() {
        List<MatchPagingData> matchList = matchService.getMatchList();
        ReadMatchesResponse response = new ReadMatchesResponse();
        if(matchList.size() == 0) {
            response.setEmpty(true);
            return ResponseEntity.ok(response);
        }
        response.setMatchPagingData(matchList);
        response.setLastIndex(matchList.get(matchList.size()-1).getMatchId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/read/{lastMatchId}")
    public ResponseEntity<?> readMatches(@PathVariable Long lastMatchId) {
        List<MatchPagingData> matchList = matchService.getMatchList(lastMatchId);
        ReadMatchesResponse response = new ReadMatchesResponse();
        if(matchList.size() == 0) {
            response.setEmpty(true);
            return ResponseEntity.ok(response);
        }
        response.setMatchPagingData(matchList);
        response.setLastIndex(matchList.get(matchList.size()-1).getMatchId());

        return ResponseEntity.ok(response);
    }
}
