package com.study.springlambda;

import com.study.springlambda.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@RequiredArgsConstructor
public class InitData {

    private final InitDataService initDataService;

    @PostConstruct
    public void init() {
        initDataService.init();
    }

    @Component
    static class InitDataService {
        @PersistenceContext
        private EntityManager em;

        @Transactional
        public void init() {
            for (int i = 0; i < 20; i++) {
                em.persist(new Member("member" + i, i));
            }
        }
    }
}
