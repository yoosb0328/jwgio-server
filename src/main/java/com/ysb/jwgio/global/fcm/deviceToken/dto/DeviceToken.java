package com.ysb.jwgio.global.fcm.deviceToken.dto;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class DeviceToken {
    private String deviceToken;
    private Long memberId;

    public DeviceToken() {}

    @DynamoDbPartitionKey
    @DynamoDbAttribute(value = "deviceToken")
    public String getDeviceToken() {
        return this.deviceToken;
    }

    @DynamoDbAttribute(value = "memberId")
    public Long getMemberId() {
        return this.memberId;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
}
