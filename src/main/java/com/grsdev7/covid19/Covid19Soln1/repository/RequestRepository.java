package com.grsdev7.covid19.Covid19Soln1.repository;


import ch.qos.logback.classic.Logger;
import com.grsdev7.covid19.Covid19Soln1.domain.Request;
import org.springframework.data.domain.Example;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface RequestRepository extends ReactiveMongoRepository<Request, Long> {


}
