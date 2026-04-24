package com.deviceshop.customer.rest;

import org.platformlambda.core.exception.AppException;
import org.platformlambda.core.models.EventEnvelope;
import org.platformlambda.core.system.PostOffice;
import org.platformlambda.core.util.Utility;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class CreateCustomerController {

    @PostMapping(value = "/api/customers", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<Map<String, Object>>> createCustomer(@RequestBody Map<String, Object> body) {
        String traceId = Utility.getInstance().getUuid();
        PostOffice po = new PostOffice("create.customer.endpoint", traceId, "POST /api/customers");
        EventEnvelope req = new EventEnvelope().setTo("customer.create").setBody(body);
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
                                callback.success(ResponseEntity.status(201).body(customer));
                            }
                        })
        );
    }
}
