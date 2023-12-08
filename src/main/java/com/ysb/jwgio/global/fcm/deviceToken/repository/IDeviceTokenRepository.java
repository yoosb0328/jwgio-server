package com.ysb.jwgio.global.fcm.deviceToken.repository;

import com.ysb.jwgio.global.fcm.deviceToken.dto.DeviceToken;

import java.util.Optional;

public interface IDeviceTokenRepository {
    void save(DeviceToken deviceToken);
    boolean existsByPk(String pk);
    Optional<DeviceToken> findByPk(String pk);
    void deleteByPk(String pk);
}
