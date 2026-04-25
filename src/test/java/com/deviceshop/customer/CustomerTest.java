package com.deviceshop.customer;

import com.deviceshop.customer.support.TestBase;
import org.junit.jupiter.api.Test;
import org.platformlambda.core.models.AsyncHttpRequest;
import org.platformlambda.core.models.EventEnvelope;
import org.platformlambda.core.system.PostOffice;
import org.platformlambda.core.util.MultiLevelMap;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest extends TestBase {
    private static final String HTTP_CLIENT = "async.http.request";
    private static final long TIMEOUT = 8000;

    @SuppressWarnings("unchecked")
    @Test
    void endToEndTest() throws ExecutionException, InterruptedException {
        PostOffice po = PostOffice.trackable("unit.test", "1000", "TEST /api/customers");

        AsyncHttpRequest req1 = new AsyncHttpRequest()
                .setTargetHost(HOST).setMethod("GET")
                .setHeader("accept", "application/json")
                .setUrl("/api/customers/9999");
        EventEnvelope result1 = po.request(new EventEnvelope().setTo(HTTP_CLIENT).setBody(req1), TIMEOUT).get();
        assertEquals(404, result1.getStatus());

        AsyncHttpRequest req2 = new AsyncHttpRequest()
                .setTargetHost(HOST).setMethod("POST")
                .setHeader("content-type", "application/json")
                .setHeader("accept", "application/json")
                .setBody(Map.of("name", "Alice", "email", "alice@example.com"))
                .setUrl("/api/customers");
        EventEnvelope result2 = po.request(new EventEnvelope().setTo(HTTP_CLIENT).setBody(req2), TIMEOUT).get();
        assertEquals(201, result2.getStatus());
        var mm2 = new MultiLevelMap((Map<String, Object>) result2.getBody());
        assertEquals("Alice", mm2.getElement("name"));
        assertEquals("alice@example.com", mm2.getElement("email"));
        assertNotNull(mm2.getElement("id"));
        long customerId = ((Number) mm2.getElement("id")).longValue();

        AsyncHttpRequest req3 = new AsyncHttpRequest()
                .setTargetHost(HOST).setMethod("GET")
                .setHeader("accept", "application/json")
                .setUrl("/api/customers/" + customerId);
        EventEnvelope result3 = po.request(new EventEnvelope().setTo(HTTP_CLIENT).setBody(req3), TIMEOUT).get();
        assertEquals(200, result3.getStatus());
        var mm3 = new MultiLevelMap((Map<String, Object>) result3.getBody());
        assertEquals("Alice", mm3.getElement("name"));
        assertEquals("alice@example.com", mm3.getElement("email"));

        AsyncHttpRequest req4 = new AsyncHttpRequest()
                .setTargetHost(HOST).setMethod("POST")
                .setHeader("content-type", "application/json")
                .setHeader("accept", "application/json")
                .setBody(Map.of("name", "Alice", "email", "alice@example.com"))
                .setUrl("/api/customers");
        EventEnvelope result4 = po.request(new EventEnvelope().setTo(HTTP_CLIENT).setBody(req4), TIMEOUT).get();
        assertEquals(409, result4.getStatus());
    }

    @Test
    void validationTest() throws ExecutionException, InterruptedException {
        PostOffice po = PostOffice.trackable("unit.test", "2000", "TEST /api/customers validation");

        AsyncHttpRequest req1 = new AsyncHttpRequest()
                .setTargetHost(HOST).setMethod("POST")
                .setHeader("content-type", "application/json")
                .setHeader("accept", "application/json")
                .setBody(Map.of("name", "", "email", "test@example.com"))
                .setUrl("/api/customers");
        EventEnvelope result1 = po.request(new EventEnvelope().setTo(HTTP_CLIENT).setBody(req1), TIMEOUT).get();
        assertEquals(400, result1.getStatus());

        AsyncHttpRequest req2 = new AsyncHttpRequest()
                .setTargetHost(HOST).setMethod("POST")
                .setHeader("content-type", "application/json")
                .setHeader("accept", "application/json")
                .setBody(Map.of("name", "Bob", "email", "not-an-email"))
                .setUrl("/api/customers");
        EventEnvelope result2 = po.request(new EventEnvelope().setTo(HTTP_CLIENT).setBody(req2), TIMEOUT).get();
        assertEquals(400, result2.getStatus());
    }
}
