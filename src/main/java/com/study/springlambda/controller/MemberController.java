package com.study.springlambda.controller;

import com.study.springlambda.entity.Member;
import com.study.springlambda.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;

    @GetMapping("/members")
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

}
