package com.helthyme.report.repository;

import com.helthyme.report.model.NutritionData;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface INutritionDataRepository {
    List<NutritionData> filterByUserIdAndDate(String userId, Long startDate, Long endDate);
}
