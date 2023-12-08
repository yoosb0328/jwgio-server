package com.ysb.jwgio.global.auth.jwt.refreshToken.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import com.ysb.jwgio.global.auth.jwt.refreshToken.dto.RefreshToken;
import java.util.Optional;
@Slf4j
@RequiredArgsConstructor
@Repository
public class RefreshTokenRepository implements IRefreshTokenRepository {

    private final DynamoDbTable<RefreshToken> table;

    @Override
    public void save(RefreshToken refreshToken) {
        table.putItem(refreshToken);
    }

    @Override
    public boolean existsByPk(String pk) {
        RefreshToken refreshTokenObj = table.getItem(Key.builder().partitionValue(pk).build());
        if (refreshTokenObj != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Optional<RefreshToken> findByPk(String pk) {
        RefreshToken refreshTokenObj = table.getItem(Key.builder().partitionValue(pk).build());
        if (refreshTokenObj != null) {
            return Optional.of(refreshTokenObj);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public void deleteByPk(String pk) { table.deleteItem(Key.builder().partitionValue(pk).build()); }
}
