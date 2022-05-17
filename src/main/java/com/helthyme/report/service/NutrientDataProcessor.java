package com.helthyme.report.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.helthyme.report.Constants;
import com.helthyme.report.domain.Item;
import com.helthyme.report.domain.NutrientReport;
import com.helthyme.report.model.NutrientItem;
import com.helthyme.report.model.NutritionData;
import com.helthyme.report.repository.INutritionDataRepository;
import com.helthyme.report.repository.NutritionDataRepository;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NutrientDataProcessor {

    private final String userId;
    private final Long fromDate;
    private final Long toDate;
    private final Map<String, Item> totalNutrientSumForItems;
    private final NutrientReport report;
    private Double totalCalories;
    private Double totalWeight;
    private final INutritionDataRepository nutritionDataRepository;


    public NutrientDataProcessor(String userId, Long fromDate, Long toDate, AmazonDynamoDB dynamoDB) {
        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.report = new NutrientReport();
        this.totalNutrientSumForItems = new HashMap<>();
        this.totalCalories = 0.0;
        this.totalWeight = 0.0;
        this.nutritionDataRepository = new NutritionDataRepository(new DynamoDBMapper(dynamoDB));
    }

    public void process() {

        List<NutritionData> nutritionDataList = this.nutritionDataRepository.filterByUserIdAndDate(userId, fromDate, toDate);
        for (NutritionData nutritionData : nutritionDataList) {
            for (Map.Entry<String, NutrientItem> itemEntry : nutritionData.getEdamamResponse().getTotalNutrients().entrySet()) {
                if (Constants.NTRCode.ENERC_KCAL.equals(itemEntry.getKey())) {
                    totalCalories += itemEntry.getValue().getQuantity();
                    continue;
                }
                double quantity = getAmountInGrams(itemEntry.getValue().getUnit(), itemEntry.getValue().getQuantity());
                if (totalNutrientSumForItems.containsKey(itemEntry.getKey())) {
                    Item item = totalNutrientSumForItems.get(itemEntry.getKey());

                    double sum = item.getAmount() + quantity;
                    item.setAmount(sum);
                } else {
                    totalNutrientSumForItems.put(itemEntry.getKey(), new Item(itemEntry.getValue().getLabel(), quantity));
                }
                totalWeight += quantity;
            }
        }
        calculatePercentages();
        createReport();

    }

    private void calculatePercentages() {
        this.totalNutrientSumForItems.forEach((s, item) -> {
            double percentage = item.getAmount() / totalWeight * 100.0;
            item.setPercentage(Math.round(percentage * 100.0) / 100.0);
        });
    }

    private void createReport() {
        this.report.setTotalCalorie(totalCalories);
        this.report.setNutrients((List<Item>) totalNutrientSumForItems.values());
        this.report.setReportId(userId + "_" + fromDate + "_" + toDate);
    }

    private double getAmountInGrams(String unit, Double quantity) {
        switch (unit) {
            case Constants.Unit.MILI_GRAM:
                return quantity / 1000.0;
            case Constants.Unit.MICRO_GRAM:
                return quantity / 1000000.0;
            default:
                return quantity;
        }
    }


}
