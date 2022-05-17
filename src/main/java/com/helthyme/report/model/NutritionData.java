package com.helthyme.report.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "nutrition-data")
public class NutritionData {

    @DynamoDBHashKey(attributeName = "user_id")
    private String userId;

    @DynamoDBRangeKey(attributeName = "created_date")
    private String createdDate;

    @DynamoDBAttribute(attributeName = "meal_id")
    private String mealId;

    @DynamoDBAttribute(attributeName = "edamamResponse")
    private NutritionAnalysis edamamResponse;
}
