package org.opentmf.common.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.opentmf.common.service.MockServerHelper.addingMockServerData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opentmf.common.client.api.GenericClient;
import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;
import org.opentmf.common.exception.TmfClientException;
import org.opentmf.common.helper.MockServerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import reactor.test.StepVerifier;

/**
 * @author Gokhan Demir
 */
@SpringBootTest
class GenericTestClientIT {

  private String path = "/test";
  @Autowired
  private TmfClientConfig testClientConfig;
  @Autowired
  private GenericClient genericTestClient;

  @BeforeEach
  void beforeEach() {
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

  @Getter
  @Setter
  static class Data {

    private String id;
    private String state;
  }

  @Test
  void testPostAndGet_withValidExpectations_succeeds() {
    MockServerUtils.setUpDynamicPostCallback(path);
    MockServerUtils.setUpDynamicGetCallback(path);
    StepVerifier.create(genericTestClient.post("{ \"id\": \"2\" }"))
        .expectNextCount(1)
        .verifyComplete();
    var getResult = StepVerifier.create(genericTestClient.get("2", Data.class))
        .expectNextMatches(data -> {
          assertEquals("2", data.getId());
          assertEquals("completed", data.getState());
          return true;
        })
        .verifyComplete();
    assertNotNull(getResult);
  }

  @Test
  @SuppressWarnings("unchecked")
  void testPostAndList_withValidExpectations_succeeds() {
    MockServerUtils.setUpDynamicPostCallback(path);
    MockServerUtils.setUpDynamicListCallback(path);
    StepVerifier.create(genericTestClient.post("{ \"id\": \"3\" }"))
        .expectNextCount(1)
        .verifyComplete();

    var list = genericTestClient.list().collectList().block();

    assertNotNull(list);
    assertEquals(1, list.size());
    assertInstanceOf(Map.class, list.get(0));
    var map = (Map<String, Object>) list.get(0);
    assertEquals("3", map.get("id"));
    assertEquals("acknowledged", map.get("state"));
  }

  @Test
  void test_getAllListWithList_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    List<Object> objectList = genericTestClient.list().collectList().block();
    assertNotNull(objectList);
    assertInstanceOf(List.class, objectList);
    assertEquals(10, objectList.size());
    assertInstanceOf(LinkedHashMap.class, objectList.get(0));
  }

  @Test
  void test_getAllListWithPageableQuery_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = genericTestClient.listAll(Pageable.ofSize(5));
    StepVerifier.create(response)
        .expectNextCount(40)
        .verifyComplete();
  }
}
