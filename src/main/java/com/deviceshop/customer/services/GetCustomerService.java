package com.deviceshop.customer.services;

import org.platformlambda.core.annotations.PreLoad;
import org.platformlambda.core.exception.AppException;
import org.platformlambda.core.models.TypedLambdaFunction;

import java.util.Map;

@PreLoad(route = "customer.get", instances = 10)
public class GetCustomerService implements TypedLambdaFunction<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleEvent(Map<String, String> headers, Map<String, Object> input, int instance) throws AppException {
        String idStr = headers.get("id");
        if (idStr == null) {
            throw new AppException(400, "Customer ID is required");
        }
        long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            throw new AppException(400, "Customer ID must be a number");
        }

        Map<String, Object> customer = CustomerStore.findById(id);
        if (customer == null) {
            throw new AppException(404, "Customer not found");
        }
        return customer;
    }
}
