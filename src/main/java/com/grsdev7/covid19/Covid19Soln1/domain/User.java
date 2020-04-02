package com.grsdev7.covid19.Covid19Soln1.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@Builder
@With
public class User {
    private String email;
    private String name;
    private String city;
}
