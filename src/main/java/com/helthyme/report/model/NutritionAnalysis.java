package com.helthyme.report.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBDocument 
public class NutritionAnalysis {
    private Map<String,NutrientItem> totalNutrients;
    private Map<String,NutrientItem> totalNutrientsKcal;

}
