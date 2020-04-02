package com.grsdev7.covid19.Covid19Soln1.controller;

import com.grsdev7.covid19.Covid19Soln1.domain.Request;
import com.grsdev7.covid19.Covid19Soln1.dto.ItemDto;
import com.grsdev7.covid19.Covid19Soln1.dto.RequestDto;
import com.grsdev7.covid19.Covid19Soln1.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Mono;

import java.util.Set;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;


    @GetMapping("/requests/form")
    public String getRequestForm(Model model) {
        log.info("Get New Request Form");
        model.addAttribute("requestDto", buildRequestDto());
        return "newRequestForm";
    }

    private RequestDto buildRequestDto() {
        return RequestDto.builder()
                .itemDtos(Set.of(ItemDto.builder().build()))
                .build();
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public String saveRequest(RequestDto requestDto, Model model) {
        log.info("Save request : {}", requestDto);
        Mono<Request> saveResult = requestService.saveRequest(requestDto);
        saveResult.subscribe(saved -> log.info("New Request saved : {}", saved));
        model.addAttribute("saveResult", new ReactiveDataDriverContextVariable(saveResult));
        return "redirect:/";
    }

}
