package com.ysb.jwgio;

import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.internal.LambdaContainerHandler;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.ZoneId;
import java.util.TimeZone;

@Slf4j
public class StreamLambdaHandler implements RequestStreamHandler {
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    static{
        try{
            log.info("@@@@@@@@@@@@@ JWGIO StreamLambdaHandler START @@@@@@@@@@@@@@@@@@@@@");
            TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")));
            log.info("TimeZone.getTimeZone = {}", TimeZone.getDefault());
            //api gateway timeout 방지
            LambdaContainerHandler.getContainerConfig().setInitializationTimeout(40_000);
            //실행속도 개선을 위해 async 방식으로 Init
            handler = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
                    .defaultProxy()
                    .asyncInit()
                    .springBootApplication(JwGioApplication.class)
                    .buildAndInitialize();

        } catch(ContainerInitializationException e){
            throw new RuntimeException("Spring Boot Application 실행 실패", e);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws RuntimeException, IOException {
        handler.proxyStream(input, output, context);
    }
}
