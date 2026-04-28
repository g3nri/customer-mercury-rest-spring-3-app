package com.deviceshop.customer.services;

import com.deviceshop.customer.models.Customer;
import com.deviceshop.customer.storage.CustomerRepository;
import org.platformlambda.core.annotations.KernelThreadRunner;
import org.platformlambda.core.annotations.PreLoad;
import org.platformlambda.core.exception.AppException;
import org.platformlambda.core.models.TypedLambdaFunction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

@PreLoad(route = "v1.customer.create", instances = 10)
@KernelThreadRunner
public class CreateCustomerService implements TypedLambdaFunction<Map<String, Object>, Map<String, Object>> {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Map<String, Object> handleEvent(Map<String, String> headers, Map<String, Object> input, int instance) throws AppException {
        if (!input.containsKey("name") || String.valueOf(input.get("name")).isBlank()) {
            throw new AppException(400, "name is required");
        }
        String name = String.valueOf(input.get("name")).trim();
        if (name.length() > 100) {
            throw new AppException(400, "name must not exceed 100 characters");
        }
        if (!input.containsKey("email") || String.valueOf(input.get("email")).isBlank()) {
            throw new AppException(400, "email is required");
        }
        String email = String.valueOf(input.get("email")).trim();
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            throw new AppException(400, "email format is invalid");
        }
        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new AppException(409, "Customer with this email already exists");
        }

        Customer customer = new Customer();
        customer.setName(name);
        customer.setEmail(email);
        customer = customerRepository.save(customer);

        Map<String, Object> result = new HashMap<>();
        result.put("id", customer.getId());
        result.put("name", customer.getName());
        result.put("email", customer.getEmail());
        return result;
    }
}