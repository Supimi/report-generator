package com.helthyme.report.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueMessage {
    private String userId;
    private Long fromDate;
    private Long endDate;
    private String fileName;
}
