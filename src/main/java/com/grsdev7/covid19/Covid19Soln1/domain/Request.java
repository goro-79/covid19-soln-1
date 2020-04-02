package com.grsdev7.covid19.Covid19Soln1.domain;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.time.Instant;
import java.util.Set;

@Document
@Value
@Builder
@With
public class Request {
    @Id
    private BigInteger id;
    private Set<Item> items;
    private User requestedBy;
    private Boolean active;
    private Long fullFilledBy;
    @CreatedDate
    private Instant createdOn;
}
