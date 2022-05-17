package com.helthyme.report;


import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.serverless.proxy.InitializationWrapper;
import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.proxy.spring.SpringBootProxyHandlerBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helthyme.report.service.ReportGenerator;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@Slf4j
public class BaseSpringStreamHandler implements RequestStreamHandler {

    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;

    public BaseSpringStreamHandler() {
        this.init();
    }

    public void init(){
        try {
            handler = new SpringBootProxyHandlerBuilder<AwsProxyRequest>()
                    .asyncInit()
                    .defaultProxy()
                    .initializationWrapper(new InitializationWrapper())
                    .springBootApplication(SprintBootApplication.class)
                    .buildAndInitialize();
        } catch (ContainerInitializationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        log.info("=====================App is starting===========================");

        ObjectMapper mapper = new ObjectMapper();
        String inputStr = IOUtils.toString(inputStream);
        Map inputMap = mapper.readValue(inputStr, Map.class);
        log.info("Request - " + inputStr);
        if (Constants.Commands.START_WEEKLY_REPORT_GENERATION.equals(inputMap.get("command"))) {
            ReportGenerator generator = new ReportGenerator();
            generator.generateReports();
        } else {
            handler.proxyStream(inputStream, outputStream, context);
        }
    }
}
