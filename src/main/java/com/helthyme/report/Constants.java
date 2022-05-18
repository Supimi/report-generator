package com.helthyme.report;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String region = "us-east-1";
    public static final String BUCKET_NAME = "helthyme-reports";
    public static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/783957005795/report-queue";
    public static final String BASE_FOLDER = "reports/weekly-reports";

    public static final class NTRCode{
        public static final String ENERC_KCAL = "ENERC_KCAL";
    }

    public static final class Unit{
        public static final String GRAM = "g";
        public static final String MILI_GRAM = "mg";
        public static final String MICRO_GRAM = "µg";

    }

    public static final class Commands{
        public static final String START_WEEKLY_REPORT_GENERATION = "start-weekly-report-generation";
    }

    public static final class NutrientGroups {
        public static final String FAT = "FAT";
        public static final List<String> FAT_GROUP = Arrays.asList("FAMS", "FAPU", "FASAT", "FATRN");
        public static final String CARBOHYDRATE = "CHOCDF";
        public static final String CARBOHYDRATE_NET = "CHOCDF.net";
        public static final List<String> CARBOHYDRATE_GROUP = Arrays.asList("FIBTG", "SUGAR", "SUGAR.added");

    }
}
