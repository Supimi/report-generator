package com.helthyme.report.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.helthyme.report.model.UserData;

import java.util.List;

public class UserDataRepository implements IUserDataRepository{
    private final DynamoDBMapper dynamoDBMapper;

    public UserDataRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    @Override
    public List<UserData> findAll() {
        return dynamoDBMapper.scan(UserData.class,new DynamoDBScanExpression());
    }
}
