package com.pia.tmf.common.service;

import static com.pia.tmf.common.service.MockServerHelper.addingMockServerData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.pia.tmf.common.client.api.GenericClient;
import com.pia.tmf.common.config.TmfClientConfigurations.TmfClientConfig;
import com.pia.tmf.common.exception.TmfClientException;
import com.pia.tmf.common.helper.MockServerUtils;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.util.LinkedMultiValueMap;
import reactor.test.StepVerifier;

/**
 * @author Gokhan Demir
 */
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
class GenericTestClientIT {

  private String path = "/test";
  @Autowired private TmfClientConfig testClientConfig;
  @Autowired private GenericClient genericTestClient;

  @BeforeAll
  void beforeAll() {
    path = "/" + RandomStringUtils.secure().nextAlphabetic(5);
    testClientConfig.setBaseUrl(MockServerUtils.BASE_URL);
    testClientConfig.setEndpoint(path);
  }

  @Test
  void testGet_withoutValidExpectations_throwsNotFound() {
    MockServerUtils.setUpDynamicGetCallback(path);
    StepVerifier.create(genericTestClient.get("non-existent-id"))
        .expectErrorMatches(error -> {
          if (error instanceof TmfClientException e) {
            assertEquals("HTTP 404 Not Found. "
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
    MockServerUtils.setUpDynamicPostCallback(path);
    MockServerUtils.setUpDynamicGetCallback(path);
    StepVerifier.create(genericTestClient.post("{ \"id\": \"1\" }" ))
        .expectNextCount(1)
        .verifyComplete();
    var getResult = StepVerifier.create(genericTestClient.get("1"))
        .expectNextCount(1)
        .verifyComplete();
    assertNotNull(getResult);
  }

  @Test
  void test_getAllListWithList_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);


    List<Object> objectList = genericTestClient.list().collectList().block();
    assertNotNull(objectList);
    assertEquals(10, objectList.size());
  }

  @Test
  void test_getAllListWithPageableQuery_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = genericTestClient.listAll(new LinkedMultiValueMap<>(), Pageable.ofSize(5));
    StepVerifier.create(response)
            .expectNextCount(40)
            .verifyComplete();
  }
}
