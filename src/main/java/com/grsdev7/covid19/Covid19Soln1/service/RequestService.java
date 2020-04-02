package com.grsdev7.covid19.Covid19Soln1.service;

import com.grsdev7.covid19.Covid19Soln1.domain.Request;
import com.grsdev7.covid19.Covid19Soln1.dto.ItemDto;
import com.grsdev7.covid19.Covid19Soln1.dto.RequestDto;
import com.grsdev7.covid19.Covid19Soln1.dto.UserDto;
import com.grsdev7.covid19.Covid19Soln1.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;

    public Flux<RequestDto> getActiveRequests(Sort sortBy, long resultLimit) {
        Example<Request> probe = getRequestProbe();
        return
                requestRepository.findAll(probe, sortBy)
                        .map(RequestDto::fromRequest)
                        .delayElements(Duration.ofSeconds(1))
                        .limitRequest(resultLimit)
                        .log()
                ;
    }

    private BigInteger getShortId(BigInteger id) {
        String idString = id.toString();
        int length = idString.length();
        String shortIdString = new StringBuilder(idString).reverse()
                .toString()
                .substring(0, Math.min(length, 5));
        return new BigInteger(shortIdString);
    }

    private Example<Request> getRequestProbe() {
        Request request = Request.builder()
                .active(true)
                .build();
        return Example.of(request);
    }

    public Mono<Request> saveRequest(RequestDto requestDto) {
        Request request = convertToRequest(requestDto);
        log.info("Converted to request : {}", request);
        Mono<Request> savedRequestMono = requestRepository.save(request);
        log.info("Saved to DB : {}", savedRequestMono);
        return savedRequestMono;
    }

    private Request convertToRequest(RequestDto requestDto) {
        return Request.builder()
                .items(ItemDto.convertToItems(requestDto.getItemDtos()))
                .requestedBy(UserDto.toUser(requestDto.getRequester()))
                .active(true)
                .build();
    }

}
