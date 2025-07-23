package com.ksy.fmrs.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface ApiClientService {

    <T> Mono<T> getApiResponse(String url, Class<T> responseType);

}
