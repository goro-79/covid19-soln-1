package com.grsdev7.covid19.Covid19Soln1.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.Instant;

@Value
@Builder
@With
public class Item {
    private String name;
    private int quantity;
    private Instant dueDate;
}
