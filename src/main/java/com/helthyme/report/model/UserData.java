package com.helthyme.report.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDBTable(tableName = "user-data")
public class UserData {

    @DynamoDBHashKey(attributeName = "user_id")
    private String userId;

    @DynamoDBAttribute(attributeName = "first_name")
    private String firstName;

    @DynamoDBAttribute(attributeName = "last_name")
    private String lastName;

    @DynamoDBIndexHashKey(attributeName = "username", globalSecondaryIndexName = "username-index")
    private String username;

    @DynamoDBAttribute(attributeName = "email")
    private String email;

    @DynamoDBAttribute(attributeName = "gender")
    private String gender;

    @DynamoDBAttribute(attributeName = "birthdate")
    private Long birthdate;

    @DynamoDBAttribute(attributeName = "weight")
    private String weight;

    @DynamoDBAttribute(attributeName = "height")
    private String height;

    @DynamoDBAttribute(attributeName = "bmi")
    private Integer bmi;

}
