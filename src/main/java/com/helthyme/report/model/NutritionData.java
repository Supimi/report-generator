package com.helthyme.report.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
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

    @DynamoDBIndexRangeKey(attributeName = "created_date")
    private String createdDate;

    @DynamoDBAttribute(attributeName = "meal_id")
    private String mealId;

    @DynamoDBAttribute(attributeName = "edamamResponse")
    private NutritionAnalysis edamamResponse;
}
