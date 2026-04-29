package com.deviceshop.customer.services;

import com.deviceshop.customer.models.Customer;
import com.deviceshop.customer.storage.CustomerRepository;
import org.platformlambda.core.annotations.PreLoad;
import org.platformlambda.core.exception.AppException;
import org.platformlambda.core.models.TypedLambdaFunction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@PreLoad(route = "v1.customer.get", instances = 10)
public class GetCustomerService implements TypedLambdaFunction<Map<String, Object>, Map<String, Object>> {

    @Autowired
    private CustomerRepository customerRepository;

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

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Customer not found"));

        Map<String, Object> result = new HashMap<>();
        result.put("id", customer.getId());
        result.put("name", customer.getName());
        result.put("email", customer.getEmail());
        return result;
    }
}