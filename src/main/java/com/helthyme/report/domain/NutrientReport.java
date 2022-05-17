package com.helthyme.report.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutrientReport {
    private Double totalCalorie;
    private List<Item> nutrients;
    private String reportId;
}
