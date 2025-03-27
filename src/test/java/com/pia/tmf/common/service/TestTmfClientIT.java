package com.pia.tmf.common.service;

import static com.pia.commons.util.JacksonUtil.jsonToObject;
import static com.pia.tmf.common.helper.MockServerUtils.addDataToMockServerCache;
import static com.pia.tmf.common.service.MockServerHelper.addingMockServerData;
import static com.pia.tmf.common.service.MockServerHelper.getTestData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.ReplaceOperation;
import com.pia.tmf.common.config.TestClientAutoConfiguration;
import com.pia.tmf.common.config.TestClientProvider;
import com.pia.tmf.common.config.TestTmfClient;
import com.pia.tmf.common.config.TmfClientConfigurations;
import com.pia.tmf.common.helper.MockServerUtils;
import com.pia.tmf.common.helper.TestResponseModel;
import com.pia.tmf.common.model.TmfOffsetRequest;
import com.pia.tmf.common.model.TmfPage;
import com.pia.tmf.common.model.TmfRequestContext;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Headers;
import org.mockserver.model.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@Import(TestClientAutoConfiguration.class)
class TestTmfClientIT {

  private String path;
  private static final String SH_TMF_CLIENTS = "sh-tmf-x-client";
  private static final String ID_JSON_FILTER = "$.id";
  private static final TmfRequestContext ID_RETRIEVAL_CONTEXT =
      TmfRequestContext.builder().withServerJsonFilter(ID_JSON_FILTER).build();

  @Autowired private TmfClientConfigurations tmfClientConfigurations;
  @Autowired private TestClientProvider testClientProvider;

  private TestTmfClient<ObjectNode, TestResponseModel, TestResponseModel> testTmfClient;

  @BeforeEach
  void beforeEach() {
    path = "/" + RandomStringUtils.secure().nextAlphabetic(5);

    var shClientConfig = tmfClientConfigurations.getTmfClients().get(SH_TMF_CLIENTS);
    shClientConfig.setBaseUrl(MockServerUtils.BASE_URL);
    shClientConfig.setEndpoint(path);
    testTmfClient =
        testClientProvider.getTmfClient(
            tmfClientConfigurations.getTmfClients().get(SH_TMF_CLIENTS), "sh");

    MockServerUtils.resetMockServer();
  }

  @Test
  void test_get_withValidId_shouldReturnOkay() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    MockServerUtils.setUpDynamicGetCallback(path);

    Mono<TestResponseModel> request = testTmfClient.get(id);
    StepVerifier.create(request)
        .assertNext(
            res -> {
              assertNotNull(res);
              assertEquals(id, res.getId());
              assertNotNull(res.getName());
              assertNotNull(res.getDescription());
            })
        .verifyComplete();
  }

  @Test
  void test_getWithJsonPath_withValidId_shouldReturnOkay() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    MockServerUtils.setUpDynamicGetCallback(path);

    Mono<TestResponseModel> request =
        testTmfClient.get(id, TmfRequestContext.builder().withServerJsonFilter("$.id").build());
    StepVerifier.create(request)
        .assertNext(
            res -> {
              assertNotNull(res);
              assertEquals(id, res.getId());
              assertNotNull(res.getName());
              assertNotNull(res.getDescription());
            })
        .verifyComplete();
  }

  @Test
  void test_getWithQueryParameter_withValidId_shouldReturnOkay() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    MockServerUtils.setUpDynamicGetCallback(path, Parameter.param("test", "testData"));

    Mono<TestResponseModel> request =
        testTmfClient.get(
            id, TmfRequestContext.builder().withQueryParameters("test", "testData").build());
    StepVerifier.create(request)
        .assertNext(
            res -> {
              assertNotNull(res);
              assertEquals(id, res.getId());
            })
        .verifyComplete();
  }

  @Test
  void test_getWithClassType_withValidId_shouldReturnOkay() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    MockServerUtils.setUpDynamicGetCallback(path);

    Mono<String> request = testTmfClient.get(id, String.class);
    StepVerifier.create(request).assertNext(Assertions::assertNotNull).verifyComplete();
  }

  @Test
  void test_getWithJsonTypeAndClassPath_withValidId_shouldReturnOkay() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    MockServerUtils.setUpDynamicGetCallback(path);

    Mono<String> request = testTmfClient.get(id, ID_RETRIEVAL_CONTEXT, String.class);
    StepVerifier.create(request).assertNext(Assertions::assertNotNull).verifyComplete();
  }

  @Test
  void test_getWithToken_withValidId_shouldReturnOkay() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    MockServerUtils.setUpDynamicGetCallback(path);

    Mono<TestResponseModel> request = testTmfClient.getWithToken("token", id, ID_RETRIEVAL_CONTEXT);
    StepVerifier.create(request).assertNext(Assertions::assertNotNull).verifyComplete();
  }

  @Test
  void test_getListWithServerLimit_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.list();
    StepVerifier.create(response).expectNextCount(10).verifyComplete();
  }

  @Test
  void test_getListWithLimit_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.list(TmfOffsetRequest.of(0, 5));
    StepVerifier.create(response).expectNextCount(5).verifyComplete();
  }

  @Test
  void test_getListWithServerLimitAndMultiValueMap_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.list((new LinkedMultiValueMap<>()));
    StepVerifier.create(response).expectNextCount(10).verifyComplete();
  }

  @Test
  void test_getListWithMultiValueMapAndPageRequest_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.list(new LinkedMultiValueMap<>(), TmfOffsetRequest.of(0, 3));
    StepVerifier.create(response).expectNextCount(3).verifyComplete();
  }

  @Test
  void test_getListWithPageableQuery_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.list(new LinkedMultiValueMap<>(), Pageable.ofSize(7));
    StepVerifier.create(response).expectNextCount(7).verifyComplete();
  }

  @Test
  void test_getAllListWithServerLimit_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.listAll();
    StepVerifier.create(response).expectNextCount(40).verifyComplete();
  }

  @Test
  void test_getAllListWithLimit_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.listAll(TmfOffsetRequest.of(0, 5));
    StepVerifier.create(response).expectNextCount(40).verifyComplete();
  }

  @Test
  void test_getAllListWithServerLimitAndMultiValueMap_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.listAll((new LinkedMultiValueMap<>()));
    StepVerifier.create(response).expectNextCount(40).verifyComplete();
  }

  @Test
  void test_getAllListWithMultiValueMapAndPageRequest_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.listAll(new LinkedMultiValueMap<>(), TmfOffsetRequest.of(10, 5));
    StepVerifier.create(response).expectNextCount(30).verifyComplete();
  }

  @Test
  void test_getAllListWithPageableQuery_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    var response = testTmfClient.listAll(new LinkedMultiValueMap<>(), Pageable.ofSize(5));
    StepVerifier.create(response).expectNextCount(40).verifyComplete();
  }

    @Test
    void test_getAllListWithPageableQueryAndMultiValueMap_shouldReturnSuccess() {
        addingMockServerData(path, 40);
        MockServerUtils.setUpDynamicGetListCallback(path, Parameter.param("test", "testData"));
        var multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.add("test", "testData");

        var response = testTmfClient.listAll(multiValueMap, Pageable.ofSize(5));
        StepVerifier.create(response).expectNextCount(40).verifyComplete();
    }

    @Test
    void test_getAllListWithPageableWithQueryAndMultiValueMap_shouldReturnSuccess() {
        addingMockServerData(path, 40);
        MockServerUtils.setUpDynamicGetListCallback(path, Parameter.param("test", "testData"));
        var multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.add("test", "testData");

        var response = testTmfClient.listAll(TmfOffsetRequest.of().withQueryParameters(multiValueMap));
        StepVerifier.create(response).expectNextCount(40).verifyComplete();
    }

  @Test
  void test_retrieveSinglePage_withServerLimit_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    Mono<TmfPage<Flux<TestResponseModel>>> response = testTmfClient.listPaged();

    StepVerifier.create(response)
        .assertNext(
            tmfPage -> {
              assertNotNull(tmfPage);
              assertEquals(10, tmfPage.getSize());
              assertEquals(4, tmfPage.getTotalPages());
              assertEquals(40, tmfPage.getTotalElements());
              assertEquals(0, tmfPage.getNumber());
              assertTrue(tmfPage.hasNext());
              assertFalse(tmfPage.isLast());
              assertNotNull(tmfPage.getContent());
            })
        .verifyComplete();
  }

  @Test
  void test_retrieveSinglePage_withTmfOffsetRequest_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    Mono<TmfPage<Flux<TestResponseModel>>> response =
        testTmfClient.listPaged(TmfOffsetRequest.of(7, 7));

    StepVerifier.create(response)
        .assertNext(
            tmfPage -> {
              assertNotNull(tmfPage);
              assertEquals(7, tmfPage.getSize());
              assertEquals(6, tmfPage.getTotalPages());
              assertEquals(40, tmfPage.getTotalElements());
              assertEquals(1, tmfPage.getNumber());
              assertTrue(tmfPage.hasNext());
              assertFalse(tmfPage.isLast());
              assertNotNull(tmfPage.getContent());
            })
        .verifyComplete();
  }

  @Test
  void test_retrieveSinglePage_withMultiValueMap_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    Mono<TmfPage<Flux<TestResponseModel>>> response =
        testTmfClient.listPaged(new LinkedMultiValueMap<>());

    StepVerifier.create(response)
        .assertNext(
            tmfPage -> {
              assertNotNull(tmfPage);
              assertEquals(10, tmfPage.getSize());
              assertEquals(4, tmfPage.getTotalPages());
              assertEquals(40, tmfPage.getTotalElements());
              assertEquals(0, tmfPage.getNumber());
              assertTrue(tmfPage.hasNext());
              assertFalse(tmfPage.isLast());
              assertNotNull(tmfPage.getContent());
            })
        .verifyComplete();
  }

  @Test
  void test_retrieveSinglePage_withMultiValueMapAndPageAbleRequest_shouldReturnSuccess() {
    addingMockServerData(path, 40);
    MockServerUtils.setUpDynamicGetListCallback(path);

    Mono<TmfPage<Flux<TestResponseModel>>> response =
        testTmfClient.listPaged(new LinkedMultiValueMap<>(), PageRequest.of(7, 5));

    StepVerifier.create(response)
        .assertNext(
            tmfPage -> {
              assertNotNull(tmfPage);
              assertEquals(5, tmfPage.getSize());
              assertEquals(8, tmfPage.getTotalPages());
              assertEquals(40, tmfPage.getTotalElements());
              assertEquals(7, tmfPage.getNumber());
              assertFalse(tmfPage.hasNext());
              assertTrue(tmfPage.isLast());
              assertNotNull(tmfPage.getContent());
            })
        .verifyComplete();
  }

  @Test
  void test_post_shouldReturnOkay() {
    ObjectNode node = getTestData();
    MockServerUtils.setUpDynamicPostCallback(path);

    Mono<TestResponseModel> request = testTmfClient.post(node);
    StepVerifier.create(request)
        .assertNext(
            res -> {
              assertNotNull(res);
              assertNotNull(res.getName());
              assertNotNull(res.getDescription());
            })
        .verifyComplete();
  }

  @Test
  void test_postWithHeaders_shouldReturnOkay() {
    ObjectNode node = getTestData();
    MockServerUtils.setUpDynamicPostCallback(
        path, Headers.headers().withEntry("test", "value1", "value2"));

    Mono<TestResponseModel> request =
        testTmfClient.post(
            node, TmfRequestContext.builder().withHeaderValues("test", "value1", "value2").build());
    StepVerifier.create(request)
        .assertNext(
            res -> {
              assertNotNull(res);
              assertNotNull(res.getName());
              assertNotNull(res.getDescription());
            })
        .verifyComplete();
  }

  @Test
  void test_postWithQueryParameter_shouldReturnOkay() {
    ObjectNode node = getTestData();
    MockServerUtils.setUpDynamicPostCallback(path, Parameter.param("test", "testData"));

    Mono<TestResponseModel> request =
        testTmfClient.post(
            node, TmfRequestContext.builder().withQueryParameters("test", "testData").build());
    StepVerifier.create(request)
        .assertNext(
            res -> {
              assertNotNull(res);
              assertNotNull(res.getName());
              assertNotNull(res.getDescription());
            })
        .verifyComplete();
  }

  @Test
  void test_postWithClassType_shouldReturnOkay() {
    ObjectNode node = getTestData();
    MockServerUtils.setUpDynamicPostCallback(path);

    Mono<String> request = testTmfClient.post(node, String.class);
    StepVerifier.create(request)
        .assertNext(
            res -> {
              assertNotNull(res);
              assertTrue(res.contains("id"));
            })
        .verifyComplete();
  }

  @Test
  void test_patchRequest_withValidData_shouldReturnSuccess() throws JsonPointerException {
    MockServerUtils.setUpDynamicJsonPatchCallback(path);
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    var patch =
        new JsonPatch(
            List.of(new ReplaceOperation(new JsonPointer("/name"), new TextNode("test_patch"))));

    Mono<TestResponseModel> response = testTmfClient.patch(id, patch);

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertNotNull(testResponseModel.getId());
              assertEquals("test_patch", testResponseModel.getName());
            })
        .verifyComplete();
  }

  @Test
  void test_patchRequest_withValidDataAndHeaders_shouldReturnSuccess() throws JsonPointerException {
    MockServerUtils.setUpDynamicJsonPatchCallback(
        path, Headers.headers().withEntry("IfMatch", "true"));
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    var patch =
        new JsonPatch(
            List.of(new ReplaceOperation(new JsonPointer("/name"), new TextNode("test_patch"))));

    Mono<TestResponseModel> response =
        testTmfClient.patch(
            id, patch, TmfRequestContext.builder().withHeaderValues("ifMatch", "true").build());

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertNotNull(testResponseModel.getId());
              assertEquals("test_patch", testResponseModel.getName());
            })
        .verifyComplete();
  }

  @Test
  void test_patchRequest_withClassType_shouldReturnSuccess() throws JsonPointerException {
    MockServerUtils.setUpDynamicJsonPatchCallback(path);
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    var patch =
        new JsonPatch(
            List.of(new ReplaceOperation(new JsonPointer("/name"), new TextNode("test_patch"))));

    Mono<String> response = testTmfClient.patch(id, patch, String.class);

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertTrue(testResponseModel.contains("id"));
              assertTrue(testResponseModel.contains("test_patch"));
            })
        .verifyComplete();
  }

  @Test
  void test_patchRequest_withMergePatch_shouldReturnSuccess() {
    MockServerUtils.setUpDynamicMergePatchCallback(path);
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    var object = jsonToObject("{\"name\":\"test_patch\"}", TestResponseModel.class);

    Mono<TestResponseModel> response = testTmfClient.patch(id, object);

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertEquals("test_patch", testResponseModel.getName());
            })
        .verifyComplete();
  }

  @Test
  void test_patchRequest_withMergePatchWithHeaderValues_shouldReturnSuccess() {
    MockServerUtils.setUpDynamicMergePatchCallback(
        path, Headers.headers().withEntry("IfMatch", "true"));
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    var object = jsonToObject("{\"name\":\"test_patch\"}", TestResponseModel.class);

    Mono<TestResponseModel> response =
        testTmfClient.patch(
            id, object, TmfRequestContext.builder().withHeaderValues("IfMatch", "true").build());

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertEquals("test_patch", testResponseModel.getName());
            })
        .verifyComplete();
  }

  @Test
  void test_patchRequest_withMergePatchAndClassType_shouldReturnSuccess() {
    MockServerUtils.setUpDynamicMergePatchCallback(path);
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);
    var object = jsonToObject("{\"name\":\"test_patch\"}", TestResponseModel.class);

    Mono<String> response = testTmfClient.patch(id, object, String.class);

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertTrue(testResponseModel.contains("test_patch"));
            })
        .verifyComplete();
  }

  @Test
  void test_deleteRequest_shouldReturnSuccess() {
    MockServerUtils.setUpDynamicDeleteCallback(path);
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(path, node);

    Mono<Void> response = testTmfClient.delete(id);

    StepVerifier.create(response).verifyComplete();
  }
}
