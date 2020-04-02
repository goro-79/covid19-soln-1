package com.grsdev7.covid19.Covid19Soln1.controller;

import com.grsdev7.covid19.Covid19Soln1.dto.ItemDto;
import com.grsdev7.covid19.Covid19Soln1.dto.RequestDto;
import com.grsdev7.covid19.Covid19Soln1.dto.UserDto;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

@ControllerAdvice
public class CommonControllerAdvice {
    private Map<String, String> commonAttributes = Map.of("title", "Covid19 Sol1");

    @ModelAttribute
    public void addGlobalModelAttributes(final Model model) {
        model.addAllAttributes(commonAttributes);
        model.addAttribute("requestDto", buildRequestDtoModel());
    }

    private RequestDto buildRequestDtoModel() {
        return RequestDto.builder()
                .itemDtos(buildItemDtosModel())
                .requester(buildUserDtoModel())
                .build();
    }

    private UserDto buildUserDtoModel() {
        return UserDto.builder()
                .build();
    }

    private Set<ItemDto> buildItemDtosModel() {
        return
                IntStream.rangeClosed(1, 5)
                        .mapToObj(index -> ItemDto.builder().build())
                        .collect(toSet());
    }
}
