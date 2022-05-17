package com.helthyme.report.repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.helthyme.report.model.NutritionData;

import java.util.HashMap;
import java.util.List;

public class NutritionDataRepository implements INutritionDataRepository {
    private final DynamoDBMapper dynamoDBMapper;

    public NutritionDataRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    @Override
    public List<NutritionData> filterByUserIdAndDate(String userId, Long startDate, Long endDate) {
        HashMap<String, AttributeValue> valueMap = new HashMap<>();
        valueMap.put(":v1", new AttributeValue().withS(userId));
        valueMap.put(":v2", new AttributeValue().withN(String.valueOf(startDate)));
        valueMap.put(":v3", new AttributeValue().withN(String.valueOf(endDate)));

        DynamoDBQueryExpression<NutritionData> query = new DynamoDBQueryExpression<NutritionData>()
                .withKeyConditionExpression("user_id = :v1 and updated_date between :v2 and :v3")
                .withExpressionAttributeValues(valueMap);

        return dynamoDBMapper.query(NutritionData.class, query);
    }
}
