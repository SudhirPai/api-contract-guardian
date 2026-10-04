package com.example.contractguardian.contract;

import org.springframework.core.io.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenApiFetcher {
    private final RestClient client = RestClient.create();

    public String fetch(String url) {
        try {
            if (url.startsWith("classpath:")) {
                Resource r = new ClassPathResource(url.substring(10));
                return new String(r.getInputStream().readAllBytes());
            }
            return client.get().uri(url).retrieve().body(String.class);
        } catch (Exception e) {
            throw new IllegalStateException("Could not fetch contract from " + url, e);
        }
    }
}