package com.helthyme.report.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helthyme.report.Constants;
import com.helthyme.report.model.UserData;
import com.helthyme.report.repository.IUserDataRepository;
import com.helthyme.report.repository.UserDataRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.List;

@Slf4j
public class ReportGenerator {
    private AmazonDynamoDB dynamoDB;

    private final IUserDataRepository IUserDataRepository;

    public ReportGenerator() {
        this.dynamoDB = AmazonDynamoDBClientBuilder.standard().withRegion(Constants.region).build();
        this.IUserDataRepository = new UserDataRepository(new DynamoDBMapper(dynamoDB));
    }

    public void generateReports() {
        List<UserData> users = this.IUserDataRepository.findAll();

        Calendar cal = Calendar.getInstance();
        long toDate = cal.getTimeInMillis();

        cal.add(Calendar.DATE, -7);
        long fromDate = cal.getTimeInMillis();

        users.forEach(user -> {
            log.info("Initiating report generation for user:{} from {} to {}", user.getUserId(), fromDate, toDate);
            NutrientDataProcessor processor = new NutrientDataProcessor(user.getUserId(), fromDate, toDate, dynamoDB, new ObjectMapper());
            processor.process();
        });
    }
}
