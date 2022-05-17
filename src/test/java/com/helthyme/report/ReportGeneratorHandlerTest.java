package com.helthyme.report;


import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext;
import com.amazonaws.services.lambda.runtime.Context;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.fail;

public class ReportGeneratorHandlerTest {

    private static BaseSpringStreamHandler handler;
    private static Context lambdaContext;

    @BeforeAll
    public static void setup() {
        lambdaContext = new MockLambdaContext();
        handler = new BaseSpringStreamHandler();
    }


    @Disabled
    @Test
    public void testApp() {
        try {
            String rq = "{\"command\":\"start-weekly-report-generation\"}";
            InputStream inputStream =  new ByteArrayInputStream(rq.getBytes(StandardCharsets.UTF_8));
            ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
            handle(inputStream, responseStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle(InputStream is, ByteArrayOutputStream os) {
        try {
            handler.handleRequest(is, os, lambdaContext);
            System.out.println(os.toString());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
