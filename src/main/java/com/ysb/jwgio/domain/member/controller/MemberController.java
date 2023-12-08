package com.ysb.jwgio.domain.member.controller;

import com.ysb.jwgio.domain.member.dto.AllMemberReadResponse;
import com.ysb.jwgio.domain.member.dto.JerseyNumberCheckResponse;
import com.ysb.jwgio.domain.member.dto.ProfileUpdateRequest;
import com.ysb.jwgio.domain.member.dto.TopPlayerRecordData;
import com.ysb.jwgio.domain.member.service.MemberService;
import com.ysb.jwgio.global.common.s3.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;
    private final S3UploadService s3UploadService;

//    @GetMapping("/members")
//    public List<Member> findAll() {
//        return memberRepository.findAll();
//    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> profileUpdate(
            @RequestPart(value = "img", required = false) MultipartFile img,
            @RequestPart(value = "dto") ProfileUpdateRequest dto) throws IOException {

        Long userId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        String imgUrl = "";
        if(!(img == null)) {
            log.info("memberController- profileUpdate : img is not empty");
            imgUrl = s3UploadService.saveFile(img);
        }
        System.out.println(dto.getName() + " " + dto.getNumber() + " " + dto.getPosition());
        return ResponseEntity.ok(memberService.updateProfile(userId, imgUrl, dto.getName(),  dto.getNumber(), dto.getPosition()));
    }

    @GetMapping("/profile/read")
    public ResponseEntity<?> profileRead(@RequestParam String member_id) {
        return ResponseEntity.ok(memberService.readProfile(Long.parseLong(member_id)));
    }

    @GetMapping("/profile/jerseyNumberCheck/{jerseyNumber}")
    public ResponseEntity<?> jerseyNumberCheck(@PathVariable int jerseyNumber) {
        JerseyNumberCheckResponse jerseyNumberCheckResponse = memberService.checkJerseyNumber(jerseyNumber);
        return ResponseEntity.ok(jerseyNumberCheckResponse);
    }

    @GetMapping("/read/all")
    public ResponseEntity<?> readAllMember() {
        List<AllMemberReadResponse> response = memberService.readAllMember();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read/top/goal")
    public ResponseEntity<?> readTopGoal() {
        List<TopPlayerRecordData> response = memberService.readTopGoal();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read/top/assist")
    public ResponseEntity<?> readTopAssist() {
        List<TopPlayerRecordData> response = memberService.readTopAssist();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read/top/rate")
    public ResponseEntity<?> readTopRate() {
        List<TopPlayerRecordData> response = memberService.readTopRate();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read/top/managerRate")
    public ResponseEntity<?> readTopManagerRate() {
        List<TopPlayerRecordData> response = memberService.readTopManagerRate();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read/top/match")
    public ResponseEntity<?> readTopMatch() {
        List<TopPlayerRecordData> response = memberService.readTopMatch();
        return ResponseEntity.ok(response);
    }
}
