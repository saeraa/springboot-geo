package com.example.springrest.geocode;

import com.example.springrest.utils.ConfigProperties;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Objects;

@Service
@EnableRetry(proxyTargetClass = true)
public class GeoCodeService {

    RestClient restClient;

    final ConfigProperties configProperties;

    public GeoCodeService(RestClient.Builder restClientBuilder, ConfigProperties configProperties) {
        this.configProperties = configProperties;
        System.out.println(configProperties.geo_base_url());
        this.restClient = restClientBuilder.baseUrl(configProperties.geo_base_url()).build();
    }

    @Retryable(retryFor = {RestClientException.class},
            backoff = @Backoff(delay = 1000))
    public GeoCode reverseGeoCode(float lat, float lon) {
        return Objects.requireNonNull(restClient.get()
                .uri(uriBuilder -> uriBuilder.path("/reverse")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .build())
                .retrieve()
                .body(GeoCode.class));
    }

    @Recover
    public String recoverMethod(float lat, float lon, RestClientException e) {
        return "Error, couldn't get value";
    }
}