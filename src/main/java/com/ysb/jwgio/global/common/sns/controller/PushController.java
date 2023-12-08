package com.ysb.jwgio.global.common.sns.controller;

import com.ysb.jwgio.global.common.converter.LocalDateTimeConverter;
import com.ysb.jwgio.global.common.sns.SnsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
public class PushController {

    private final SnsService snsService;

    @GetMapping("/match/create/{username}/{stadiumIndex}/{date}")
    public ResponseEntity<?> pushCreateMatch(@PathVariable String username, @PathVariable int stadiumIndex, @PathVariable String date) {
        snsService.pubCreateMatchTopic(username, stadiumIndex, date);
        return ResponseEntity.ok(200);
    }
    @GetMapping("/match/complete")
    public ResponseEntity<?> pushCompleteMatch(){
        snsService.pubCompleteMatchTopic();
        return ResponseEntity.ok(200);
    }
}
