package com.ysb.jwgio.global.auth.jwt.refreshToken.repository;


import com.ysb.jwgio.global.auth.jwt.refreshToken.dto.RefreshToken;

import java.util.Optional;

public interface IRefreshTokenRepository {
    void save(RefreshToken refreshToken);
    boolean existsByPk(String pk);
    Optional<RefreshToken> findByPk(String pk);
    void deleteByPk(String pk);
}
