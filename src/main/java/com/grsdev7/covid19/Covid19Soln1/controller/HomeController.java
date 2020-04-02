package com.grsdev7.covid19.Covid19Soln1.controller;

import com.grsdev7.covid19.Covid19Soln1.configs.HomeBannerConfig;
import com.grsdev7.covid19.Covid19Soln1.domain.Request;
import com.grsdev7.covid19.Covid19Soln1.dto.RequestDto;
import com.grsdev7.covid19.Covid19Soln1.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {
    private final RequestService requestService;
    private final HomeBannerConfig homeBannerConfig;

    @RequestMapping(path = "/")
    public String getHome(final Model model) {
        Flux<RequestDto> requestQueue = requestService.getActiveRequests(getSortBy(), 10);
        model.addAttribute("requestQueue", new ReactiveDataDriverContextVariable(requestQueue, 1));
        return "home";
    }

    private Sort getSortBy() {
        return Sort.by("createdOn").descending();
    }


    @ModelAttribute
    public HomeBannerConfig homeBannerConfig() {
        log.info("adding home banner config : {} ", homeBannerConfig);
        return homeBannerConfig;
    }

}