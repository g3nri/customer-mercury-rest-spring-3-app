package com.deviceshop.customer.services;

import org.platformlambda.core.annotations.PreLoad;
import org.platformlambda.core.exception.AppException;
import org.platformlambda.core.models.TypedLambdaFunction;

import java.util.HashMap;
import java.util.Map;

@PreLoad(route = "customer.create", instances = 10)
public class CreateCustomerService implements TypedLambdaFunction<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleEvent(Map<String, String> headers, Map<String, Object> input, int instance) throws AppException {
        String name = (String) input.get("name");
        String email = (String) input.get("email");

        if (name == null || name.isBlank()) {
            throw new AppException(400, "name is required");
        }
        if (name.length() > 100) {
            throw new AppException(400, "name must not exceed 100 characters");
        }
        if (email == null || email.isBlank()) {
            throw new AppException(400, "email is required");
        }
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new AppException(400, "email format is invalid");
        }
        if (CustomerStore.emailExists(email)) {
            throw new AppException(409, "Customer with this email already exists");
        }

        long id = CustomerStore.nextId();
        Map<String, Object> customer = new HashMap<>();
        customer.put("id", id);
        customer.put("name", name);
        customer.put("email", email);
        CustomerStore.save(id, customer);

        return customer;
    }
}
