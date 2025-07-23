package com.ksy.fmrs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
@Service
public class RestClientService {

    private final RestClient restClient;

    // API 공통 호출 메서드
    public  <T> T getApiResponse(String url, Class<T> responseType) {
        ResponseEntity<T> responseEntity = restClient
                .get()
                .uri(url)
                .retrieve()
                .toEntity(responseType);

        return validateResponse(responseEntity, url);
    }

    private <T> T validateResponse(ResponseEntity<T> responseEntity, String url) {
        if (!responseEntity.getStatusCode().is2xxSuccessful() || responseEntity.getBody() == null) {
            throw new IllegalArgumentException("API 호출 결과가 없습니다. URL: " + url);
        }
        return responseEntity.getBody();
    }
}
