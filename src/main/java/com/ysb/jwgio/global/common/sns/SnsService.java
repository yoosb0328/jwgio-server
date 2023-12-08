package com.ysb.jwgio.global.common.sns;

import com.ysb.jwgio.domain.stadium.Stadiums;
import com.ysb.jwgio.global.common.converter.LocalDateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import org.json.simple.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnsService {

    private final SnsClient snsClient;
    private final LocalDateTimeConverter localDateTimeConverter;

    @Value("${aws.sns.platformApplicationArn}")
    private String platformApplicationArn;
    @Value("${aws.sns.topic.create-match}")
    private String createMatchTopic;
    @Value("${aws.sns.topic.complete-match}")
    private String completeMatchTopic;
    @Value("${aws.sns.push-logo}")
    private String pushLogo;
    /*
    deviceToken으로 플랫폼 애플리케이션에 대한 엔드포인트를 생성하여 반환 함.
     */
    public String createEndpoint(String deviceToken) {
        CreatePlatformEndpointRequest endpointRequest = CreatePlatformEndpointRequest.builder()
                .token(deviceToken)
                .platformApplicationArn(platformApplicationArn)
                .build();

        CreatePlatformEndpointResponse response = snsClient.createPlatformEndpoint(endpointRequest);
        return response.endpointArn();
    }

    public void subCreateMatchTopic(String endpointArn) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("application")
                .endpoint(endpointArn)
                .returnSubscriptionArn(true)
                .topicArn(createMatchTopic)
                .build();

        SubscribeResponse result = snsClient.subscribe(request);
    }

    public void subCompleteMatchTopic(String endpointArn) {
        SubscribeRequest request = SubscribeRequest.builder()
                .protocol("application")
                .endpoint(endpointArn)
                .returnSubscriptionArn(true)
                .topicArn(completeMatchTopic)
                .build();

        SubscribeResponse result = snsClient.subscribe(request);
    }

    public void pubCreateMatchTopic(String username, int stadiumIndex, String date) {
        JSONObject messageJSON = new JSONObject();
        LocalDateTime localDateTime = localDateTimeConverter.ISOStringToLocalDateTimeHHmmSS(date);
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int year = localDateTime.getYear();
        String month = localDateTimeConverter.MonthtoString(localDateTime.getMonth());
        int day = localDateTime.getDayOfMonth();
        String dayOfWeek = localDateTimeConverter.DayOfWeekToString(localDateTime.getDayOfWeek());
        String stadium = Stadiums.findStadium(stadiumIndex).getName();
        messageJSON.put("default", "sample fallback message");
        if(minute == 0) {
            messageJSON.put("GCM", "{\"notification\": {\"body\": \""+year+"년 "+month+" "+day+"일 "+""+dayOfWeek+" "+hour+"시 00분"+ "\\n" +stadium+"\", \"icon\": \""+pushLogo+"\", \"title\":\""+username+"님이 매치를 등록했습니다.\", \"click_action\": \"https://www.jwgio.com\"}}");


        } else {
            messageJSON.put("GCM", "{\"notification\": {\"body\": \""+year+"년 "+month+" "+day+"일 "+""+dayOfWeek+" "+hour+"시 "+minute+"분 " + "\\n" +stadium+"\", \"icon\": \""+pushLogo+"\", \"title\":\""+username+"님이 매치를 등록했습니다.\", \"click_action\": \"https://www.jwgio.com\"}}");

        }
        System.out.println(messageJSON);
        PublishRequest request = PublishRequest.builder()
                .message(messageJSON.toString())
                .messageStructure("json")
                .topicArn(createMatchTopic)
                .build();

        PublishResponse result = snsClient.publish(request);
    }

    public void pubCompleteMatchTopic() {
        JSONObject messageJSON = new JSONObject();
        messageJSON.put("default", "sample fallback message");
        messageJSON.put("GCM", "{ \"notification\": { \"body\": \"지금 확인해보세요! \", \"icon\":\""+pushLogo+"\", \"title\":\"경기 결과가 업데이트 되었습니다.\"}}");
        PublishRequest request = PublishRequest.builder()
                .message(messageJSON.toString())
                .messageStructure("json")
                .topicArn(completeMatchTopic)
                .build();

        PublishResponse result = snsClient.publish(request);
    }
}
