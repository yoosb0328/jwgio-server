package com.ysb.jwgio.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//import static com.ysb.jwgio.global.config.JpaRepoConfig.MEMBER_REPOSITORY_PACKAGE;
//
///**
// * Spring-Data-DyanamoDB와 함께 사용 시, DynamoDB Repo 스캔을 방지하기 위해 JPA Repo 패키지 위치를 스캔 범위로 지정하는 설정 파일
// */
//@RequiredArgsConstructor
//@Configuration
//@EnableJpaRepositories(basePackages = {MEMBER_REPOSITORY_PACKAGE})
//@EnableConfigurationProperties({JpaProperties.class, HibernateProperties.class})
//public class JpaRepoConfig {
//    static final String MEMBER_REPOSITORY_PACKAGE = "com.ysb.jwgio.domain.member.repository";
//}
