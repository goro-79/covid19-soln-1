package com.grsdev7.covid19.Covid19Soln1.dto;


import com.grsdev7.covid19.Covid19Soln1.domain.Request;
import lombok.Builder;
import lombok.Data;
import lombok.With;

import java.math.BigInteger;
import java.util.Set;

@Data
@Builder
@With
public class RequestDto {
    private BigInteger id;
    private Set<ItemDto> itemDtos;
    private UserDto requester;

    public static RequestDto fromRequest(Request request) {
        return RequestDto.builder()
                .id(request.getId())
                .itemDtos(ItemDto.convertToItemDtos(request.getItems()))
                .requester(UserDto.from(request.getRequestedBy()))
                .build();
    }
}
