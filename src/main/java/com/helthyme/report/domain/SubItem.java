package com.helthyme.report.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubItem {
    public SubItem(String name, Double value) {
        this.name = name;
        this.value = value;
    }

    private String name;
    private Double value;
    private Double valuePercentage;
}
