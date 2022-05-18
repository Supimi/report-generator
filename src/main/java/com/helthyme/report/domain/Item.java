package com.helthyme.report.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    public Item(String label, double amount) {
        this.label = label;
        this.amount = amount;
    }

    private String label;
    private double amount;
    private double percentage;
    private List<SubItem> breakdown = new ArrayList<>();
}
