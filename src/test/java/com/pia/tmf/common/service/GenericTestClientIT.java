package com.pia.tmf.common.service;

import com.pia.tmf.common.client.api.GenericClient;
import com.pia.tmf.common.config.TmfClientConfigurations.TmfClientConfig;
import com.pia.tmf.common.exception.TmfClientException;
import com.pia.tmf.common.helper.MockServerUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import reactor.test.StepVerifier;

/**
 * @author Gokhan Demir
 */
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class GenericTestClientIT {

  @Autowired private TmfClientConfig testClientConfig;
  @Autowired private GenericClient genericTestClient;

  @BeforeAll
  void beforeAll() {
    testClientConfig.setBaseUrl(MockServerUtils.BASE_URL);
  }

  @Test
  void testGet_withoutValidExpectations_throwsNotFound() {
    MockServerUtils.setUpDynamicGetCallback("/test");
    StepVerifier.create(genericTestClient.get("non-existent-id"))
        .expectErrorMatches(error -> {
          if (error instanceof TmfClientException e) {
            Assertions.assertEquals("HTTP 404 Not Found. "
                + "Reason: The server has not found anything matching the Request-URI. "
                + "Message: Repeat the request with new or updated Request-URI", e.getMessage());
            Assertions.assertSame(HttpStatusCode.valueOf(404), e.getStatusCode());
            return true;
          }
          return false;
        })
        .verify();
  }

  @Test
  void testPostAndGet_withValidExpectations_succeeds() {
    MockServerUtils.setUpDynamicPostCallback("/test");
    MockServerUtils.setUpDynamicGetCallback("/test");
    StepVerifier.create(genericTestClient.post("{ \"id\": \"1\" }" ))
        .expectNextCount(1)
        .verifyComplete();
    StepVerifier.create(genericTestClient.get("1"))
        .expectNextCount(1)
        .verifyComplete();
  }
}
