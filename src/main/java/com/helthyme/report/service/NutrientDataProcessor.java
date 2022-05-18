package com.helthyme.report.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.helthyme.report.Constants;
import com.helthyme.report.domain.Item;
import com.helthyme.report.domain.NutrientReport;
import com.helthyme.report.domain.QueueMessage;
import com.helthyme.report.domain.SubItem;
import com.helthyme.report.model.NutrientItem;
import com.helthyme.report.model.NutritionData;
import com.helthyme.report.repository.INutritionDataRepository;
import com.helthyme.report.repository.NutritionDataRepository;
import com.helthyme.report.util.InternalServerException;
import lombok.extern.slf4j.Slf4j;


import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.util.Comparator.comparing;

@Slf4j
public class NutrientDataProcessor {

    private final String userId;
    private final Long fromDate;
    private final Long toDate;
    private final Map<String, Item> totalNutrientSumForItems;
    private final NutrientReport report;
    private Double totalCalories;
    private Double totalWeight;
    private final INutritionDataRepository nutritionDataRepository;
    private final S3FileManager s3FileManager;
    private final ObjectMapper objectMapper;
    private final ReportQueueService reportQueueService;


    public NutrientDataProcessor(String userId, Long fromDate, Long toDate, AmazonDynamoDB dynamoDB, ObjectMapper objectMapper) {
        this.userId = userId;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.objectMapper = objectMapper;
        this.report = new NutrientReport();
        this.totalNutrientSumForItems = new HashMap<>();
        this.totalCalories = 0.0;
        this.totalWeight = 0.0;
        this.nutritionDataRepository = new NutritionDataRepository(new DynamoDBMapper(dynamoDB));
        this.s3FileManager = new S3FileManager();
        this.reportQueueService = new ReportQueueService(objectMapper);
    }

    public void process() {

        List<NutritionData> nutritionDataList = this.nutritionDataRepository.filterByUserIdAndDate(userId, fromDate, toDate);
        for (NutritionData nutritionData : nutritionDataList) {

            Map<String, NutrientItem> nutrientItemMap = nutritionData.getEdamamResponse().getTotalNutrients();
            if (nutrientItemMap.containsKey(Constants.NutrientGroups.FAT)) {
                NutrientItem nutrientItem = nutrientItemMap.get(Constants.NutrientGroups.FAT);
                double quantity = getAmountInGrams(nutrientItem.getUnit(), nutrientItem.getQuantity());
                totalNutrientSumForItems.put(Constants.NutrientGroups.FAT, new Item(nutrientItem.getLabel(),
                        quantity));
                this.totalWeight += quantity;
                nutrientItemMap.remove(Constants.NutrientGroups.FAT);
            }
            if (nutrientItemMap.containsKey(Constants.NutrientGroups.CARBOHYDRATE)) {
                NutrientItem nutrientItem = nutrientItemMap.get(Constants.NutrientGroups.CARBOHYDRATE);
                double quantity = getAmountInGrams(nutrientItem.getUnit(), nutrientItem.getQuantity());
                totalNutrientSumForItems.put(Constants.NutrientGroups.CARBOHYDRATE, new Item("Carbohydrate",
                        quantity));
                this.totalWeight += quantity;
                nutrientItemMap.remove(Constants.NutrientGroups.CARBOHYDRATE);
            }

            nutrientItemMap.remove(Constants.NutrientGroups.CARBOHYDRATE_NET);

            for (Map.Entry<String, NutrientItem> itemEntry : nutrientItemMap.entrySet()) {
                if (Constants.NTRCode.ENERC_KCAL.equals(itemEntry.getKey())) {
                    totalCalories += itemEntry.getValue().getQuantity();
                    continue;
                }
                if (Constants.NutrientGroups.FAT_GROUP.contains(itemEntry.getKey())) {
                    Item item = totalNutrientSumForItems.get(Constants.NutrientGroups.FAT);
                    double q = getAmountInGrams(itemEntry.getValue().getUnit(), itemEntry.getValue().getQuantity());
                    item.getBreakdown().add(new SubItem(itemEntry.getValue().getLabel(), q));

                } else if (Constants.NutrientGroups.CARBOHYDRATE_GROUP.contains(itemEntry.getKey())) {
                    Item item = totalNutrientSumForItems.get(Constants.NutrientGroups.CARBOHYDRATE);
                    double q = getAmountInGrams(itemEntry.getValue().getUnit(), itemEntry.getValue().getQuantity());
                    item.getBreakdown().add(new SubItem(itemEntry.getValue().getLabel(), q));

                } else {
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
        }
        calculatePercentages();
        createReport();
        sendQueueMessage();

    }

    private void calculatePercentages() {
        this.totalNutrientSumForItems.forEach((s, item) -> {
            double percentage = item.getAmount() / totalWeight * 100.0;
            item.setPercentage(Math.round(percentage * 100.0) / 100.0);
            double roundedAmount = Math.round(item.getAmount() * 100.0) / 100.0;
            item.setAmount(roundedAmount);

            item.getBreakdown().forEach(subItem -> {
                double percent = subItem.getBAmount() / totalWeight * 100.0;
                double roundedBAmount = Math.round(subItem.getBAmount() * 100.0) / 100.0;
                subItem.setValue(Math.round(percent * 100.0) / 100.0);
                subItem.setBAmount(roundedBAmount);
            });

        });
    }

    private void createReport() {
        this.report.setTotalCalorie(totalCalories);
        String filName = userId + "_" + fromDate + "_" + toDate;
        List<Item> list = new ArrayList<>(totalNutrientSumForItems.values());
        list.sort(comparing(Item::getPercentage).reversed());
        this.report.setNutrients(list);
        this.report.setReportId(filName);

        try {
            String fileContent = objectMapper.writeValueAsString(report);
            String filePath = Constants.BASE_FOLDER + "/" + userId + "/" + filName + ".json";
            s3FileManager.upload(fileContent.getBytes(StandardCharsets.UTF_8), filePath);

        } catch (Exception | InternalServerException e) {
            log.error("Error occurred", e);
        }
    }

    private void sendQueueMessage() {
        QueueMessage message = QueueMessage.builder()
                .fileName(Constants.BASE_FOLDER + "/" + userId + "/" + report.getReportId() + ".json")
                .fromDate(fromDate)
                .endDate(toDate)
                .userId(userId)
                .build();
        reportQueueService.sendMessage(message);
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
