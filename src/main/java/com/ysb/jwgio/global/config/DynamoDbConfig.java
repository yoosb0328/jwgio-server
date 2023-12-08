package com.ysb.jwgio.global.config;

import com.ysb.jwgio.global.auth.jwt.refreshToken.dto.RefreshToken;
import com.ysb.jwgio.global.fcm.deviceToken.dto.DeviceToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/**
 * DynamoDB Repository의 패키지 위치를 @EnableDynamoDBRepositories로 등록해주어야 함.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class DynamoDbConfig {

    @Value("${aws.iam.access-key}")
    private String amazonAWSAccessKey;
    @Value("${aws.iam.secret-key}")
    private String amazonAWSSecretKey;
    @Value("${aws.dynamodb.refreshToken.table-name}")
    private String refreshTokenTableName;
    @Value("${aws.dynamodb.deviceToken.table-name}")
    private String deviceTokenTableName;
    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey)))
                .region(Region.AP_NORTHEAST_2)
                .build();

        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Bean
    public DynamoDbTable<RefreshToken> refreshTokenTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(refreshTokenTableName, TableSchema.fromBean(RefreshToken.class));
    }

    @Bean
    public DynamoDbTable<DeviceToken> deviceTokenTable(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        return dynamoDbEnhancedClient.table(deviceTokenTableName, TableSchema.fromBean(DeviceToken.class));
    }
}
