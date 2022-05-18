package com.helthyme.report.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubItem {
    public SubItem(String name, Double bAmount) {
        this.name = name;
        this.bAmount = bAmount;
    }

    private String name;
    @JsonProperty("bAmount")
    private Double bAmount;
    private Double value;
}
