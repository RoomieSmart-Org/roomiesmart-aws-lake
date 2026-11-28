package com.roomiesmart.identity.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.roomiesmart.identity.infrastructure.adapter.in.rest.HealthPingController;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class IdentityAuthSmokeTest {

    @Test
    void pingEndpointReturnsUpStatus() {
        var controller = new HealthPingController();
        var response = controller.ping();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("UP", response.getBody().get("status"));
    }
}