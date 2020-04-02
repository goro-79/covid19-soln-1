package com.grsdev7.covid19.Covid19Soln1.utils;

import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

public interface CommonUtils {

    static ReactiveDataDriverContextVariable wrap(Object object) {
        return new ReactiveDataDriverContextVariable(object);
    }
}
