package com.utility.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConsumerGrowthResponse {
    private String month;      
    private long consumerCount;
}
