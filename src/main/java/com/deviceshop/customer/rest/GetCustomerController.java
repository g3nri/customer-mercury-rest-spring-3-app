package com.deviceshop.customer.rest;

import org.platformlambda.core.exception.AppException;
import org.platformlambda.core.models.EventEnvelope;
import org.platformlambda.core.system.PostOffice;
import org.platformlambda.core.util.Utility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class GetCustomerController {

    @GetMapping(value = "/api/customers/{id}", produces = "application/json")
    public Mono<ResponseEntity<Map<String, Object>>> getCustomer(@PathVariable Long id) {
        String traceId = Utility.getInstance().getUuid();
        PostOffice po = new PostOffice("get.customer.endpoint", traceId, "GET /api/customers/" + id);
        EventEnvelope req = new EventEnvelope()
                .setTo("v1.customer.get")
                .setHeader("id", id.toString());
        return Mono.create(callback ->
                po.eRequest(req, 10000, false)
                        .thenAccept(response -> {
                            if (response.hasError()) {
                                var error = response.getError() instanceof String text
                                        ? text : String.valueOf(response.getError());
                                callback.error(new AppException(response.getStatus(), error));
                            } else {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> customer = (Map<String, Object>) response.getBody();
                                callback.success(ResponseEntity.ok(customer));
                            }
                        })
        );
    }
}
