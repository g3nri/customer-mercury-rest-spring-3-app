package com.deviceshop.customer.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CustomerStore {
    private static final AtomicLong idSequence = new AtomicLong(1);
    private static final ConcurrentHashMap<Long, Map<String, Object>> store = new ConcurrentHashMap<>();

    private CustomerStore() {}

    public static long nextId() {
        return idSequence.getAndIncrement();
    }

    public static void save(long id, Map<String, Object> customer) {
        store.put(id, customer);
    }

    public static Map<String, Object> findById(long id) {
        return store.get(id);
    }

    public static boolean emailExists(String email) {
        return store.values().stream()
                .anyMatch(c -> email.equalsIgnoreCase((String) c.get("email")));
    }
}
