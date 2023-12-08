package com.ysb.jwgio.global.fcm.deviceToken.repository;

import com.ysb.jwgio.global.fcm.deviceToken.dto.DeviceToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class DeviceTokenRepository implements IDeviceTokenRepository {

    private final DynamoDbTable<DeviceToken> table;
    @Override
    public void save(DeviceToken deviceToken) { table.putItem(deviceToken); }

    @Override
    public boolean existsByPk(String pk) {
        DeviceToken deviceTokenObj = table.getItem(Key.builder().partitionValue(pk).build());
        if(deviceTokenObj != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<DeviceToken> findByPk(String pk) {
        DeviceToken deviceTokenObj = table.getItem(Key.builder().partitionValue(pk).build());
        if(deviceTokenObj != null) {
            return Optional.of(deviceTokenObj);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByPk(String pk) { table.deleteItem(Key.builder().partitionValue(pk).build()); }
}
