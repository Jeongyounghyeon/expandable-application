package com.study.app.client;

import com.study.app.model.request.AuthenticationCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "authentication-service", url = "${feign.client.url.authentication-service}")
public interface AuthenticationClient {

    @GetMapping("/access")
    Long authenticate(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearerToken);

    @PostMapping
    void createAuthentication(@RequestBody AuthenticationCreateRequest request);
}
