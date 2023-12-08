package com.ysb.jwgio.global.common.auditing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Slf4j
@Component
public class LoginUserAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||  authentication.getName().equals("anonymousUser")) {
            log.debug("Not found authentication");
            return null;
        }
        String name = authentication.getName();
        log.debug("Found authentication.getName(): {}", name);
        return Optional.of(Long.parseLong(name));

    }
}
