package com.pia.tmf.common.util;

import static com.pia.commons.util.JacksonUtil.jsonToObject;
import static com.pia.tmf.common.model.Scope.POST;
import static com.pia.tmf.common.util.TmfClientCommonHeaderUtil.prepareHeaderConsumer;
import static com.pia.tmf.common.util.TmfClientCommonUtil.buildUri;
import static com.pia.tmf.common.util.TmfClientCommonUtil.buildUriWithId;
import static com.pia.tmf.common.util.TmfClientCommonUtil.deleteRequest;
import static com.pia.tmf.common.util.TmfClientCommonUtil.getAllRequest;
import static com.pia.tmf.common.util.TmfClientCommonUtil.getRequest;
import static com.pia.tmf.common.util.TmfClientCommonUtil.getToken;
import static com.pia.tmf.common.util.TmfClientCommonUtil.patchRequest;
import static com.pia.tmf.common.util.TmfClientCommonUtil.postRequest;
import static com.pia.tmf.common.util.TmfClientCommonUtil.retrieveAllPages;
import static com.pia.tmf.common.util.TmfClientCommonUtil.retrieveSinglePageWithPageResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.ReplaceOperation;
import com.pia.client.common.service.api.TokenService;
import com.pia.client.openid.model.OpenidClientProperties;
import com.pia.client.openid.service.api.OpenidTokenServiceMockImpl;
import com.pia.commons.util.JacksonUtil;
import com.pia.mockserver.callback.DynamicPostCallback;
import com.pia.tmf.common.config.TmfClientConfigurations;
import com.pia.tmf.common.exception.TmfClientException;
import com.pia.tmf.common.helper.MockServerUtils;
import com.pia.tmf.common.helper.TestResponseModel;
import com.pia.tmf.common.model.ErrorMessage;
import com.pia.tmf.common.model.JsonFilter;
import com.pia.tmf.common.model.TmfOffsetRequest;
import com.pia.tmf.common.model.TmfRequestContext;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.model.Header;
import org.mockserver.model.Headers;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author Gokhan Demir
 * @author Yusuf BOZKURT
 */
@SpringBootTest
class TmfClientCommonUtilIT {

  static final String SH_TMF_CLIENTS = "sh-tmf-x-client";
  static final String APIX_TMF_CLIENTS = "apix-tmf-x-client";
  static final String DXL_TMF_CLIENTS = "dxl-tmf-x-client";

  static String TMF_PATH = "/test";

  @Autowired WebClient shWebClient;
  @Autowired TmfClientConfigurations configurations;
  @Autowired TokenService shTokenService;
  @Autowired OpenidClientProperties shClientProperties;

  @BeforeEach
  void beforeEach() {
    if (MockServerUtils.clientAndServer.isRunning()) {
      MockServerUtils.clientAndServer.reset();
    }

    configurations.getTmfClients().get(SH_TMF_CLIENTS).setBaseUrl(MockServerUtils.BASE_URL);
    TMF_PATH = "/" + RandomStringUtils.randomNumeric(5);
    var shClientConfig = configurations.getTmfClients().get(SH_TMF_CLIENTS);
    shClientConfig.setEndpoint(TMF_PATH);
    MockServerUtils.setUpDynamicPostCallback(TMF_PATH);
    MockServerUtils.setUpDynamicGetCallback(TMF_PATH);
    MockServerUtils.setUpDynamicGetListCallback(TMF_PATH);

    configurations.getTmfClients().get(APIX_TMF_CLIENTS).setBaseUrl(MockServerUtils.BASE_URL);
    configurations.getTmfClients().get(DXL_TMF_CLIENTS).setBaseUrl(MockServerUtils.BASE_URL);
  }

  @Test
  void test_getToken_withScopeInTmfConfig_shouldReturnToken() {
    var results =
        getToken(
            POST, configurations.getTmfClients().get(APIX_TMF_CLIENTS), new OpenidTokenServiceMockImpl());
    StepVerifier.create(results).assertNext(Assertions::assertNotNull).verifyComplete();
  }

  @Test
  void test_getToken_withNoScopeInTmfConfig_shouldReturnToken() {
    var results =
        getToken(
            POST, configurations.getTmfClients().get(DXL_TMF_CLIENTS), new OpenidTokenServiceMockImpl());
    StepVerifier.create(results).assertNext(Assertions::assertNotNull).verifyComplete();
  }

  @Test
  void test_buildIdUri_withNoId_shouldReturnAnNPE() {
    var clientConfig = configurations.getTmfClients().get(DXL_TMF_CLIENTS);

    Assertions.assertThrows(NullPointerException.class, () -> buildUriWithId(clientConfig, null));
  }

  @Test
  void test_buildUri_withValidId_shouldReturnAValidURI() {
    var clientConfig = configurations.getTmfClients().get(DXL_TMF_CLIENTS);
    var results = buildUriWithId(clientConfig, "2");
    Assertions.assertEquals("/deleteX/2", results.getPath());
  }

  @Test
  void test_buildUri_withValidData_shouldReturnAValidURI() {
    var tmfClient = configurations.getTmfClients().get(SH_TMF_CLIENTS);
    var results = buildUri(tmfClient);
    Assertions.assertEquals(tmfClient.getBaseUrl() + tmfClient.getEndpoint(), results.toString());
  }

  @Test
  void testBuildUriWithMultiValueMap() {
    // Arrange
    var clientConfig = configurations.getTmfClients().get(SH_TMF_CLIENTS);
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("param1", "value1");
    queryParams.add("param2", "value2");

    // Act
    URI result = TmfClientCommonUtil.buildUri(clientConfig, queryParams);

    // Assert
    assertEquals(
        clientConfig.getBaseUrl() + clientConfig.getEndpoint() + "?param1=value1&param2=value2",
        result.toString());
  }

  @Test
  void testBuildUriWithMultiValueMapWithSameParam() {
    // Arrange
    var clientConfig = configurations.getTmfClients().get(SH_TMF_CLIENTS);
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("param1", "value11");
    queryParams.add("param1", "value12");
    queryParams.add("param2", "value21");
    queryParams.add("param2", "value22");

    // Act
    URI result = TmfClientCommonUtil.buildUri(clientConfig, queryParams);

    // Assert
    assertEquals(
        clientConfig.getBaseUrl()
            + clientConfig.getEndpoint()
            + "?param1=value11&param1=value12&param2=value21&param2=value22",
        result.toString());
  }

  @Test
  void testBuildUri() {
    var clientConfig = configurations.getTmfClients().get(SH_TMF_CLIENTS);

    String id = "testId";

    TmfRequestContext requestContext = new TmfRequestContext();
    requestContext.setJsonFilter(JsonFilter.of("query", JsonFilter.TYPE.SERVER));
    requestContext.setFields(Set.of("field1"));

    URI result = TmfClientCommonUtil.buildUriWithId(clientConfig, id, requestContext);

    assertEquals(
        clientConfig.getBaseUrl()
            + clientConfig.getEndpoint()
            + "/testId?filter=query&fields=field1",
        result.toString());
  }

  @Test
  void testBuildUriWithNullId() {
    TmfClientConfigurations.TmfClientConfig config = new TmfClientConfigurations.TmfClientConfig();
    TmfRequestContext requestContext = new TmfRequestContext();

    assertThrows(
        NullPointerException.class,
        () -> TmfClientCommonUtil.buildUriWithId(config, null, requestContext));
  }

  @Test
  void test_postRequest_withNoAccessToken_shouldReturnNPE() {
    var clientConfig = configurations.getTmfClients().get(APIX_TMF_CLIENTS);
    var uri = buildUriWithId(clientConfig, "2");
    Assertions.assertThrows(IllegalArgumentException.class, () ->
        postRequest(
            shWebClient,
            uri,
            ErrorMessage.class,
            prepareHeaderConsumer(null, null),
            ErrorMessage.class,
            null,
            shClientProperties));
  }

  @Test
  void test_postRequest_withNoRequestBody_shouldReturnNPE() {
    var clientConfig = configurations.getTmfClients().get(APIX_TMF_CLIENTS);
    var uri = buildUriWithId(clientConfig, "2");
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    postRequest(
                        shWebClient,
                        uri,
                        null,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        ErrorMessage.class,
                        null,
                        shClientProperties));
    StepVerifier.create(response).expectError(NullPointerException.class).verify();
  }

  @Test
  void test_patchRequest_withNoAccessToken_shouldReturnNPE() {
    var clientConfig = configurations.getTmfClients().get(APIX_TMF_CLIENTS);
    var uri = buildUriWithId(clientConfig, "2");
    Assertions.assertThrows(
        NullPointerException.class,
        () ->
            patchRequest(
                shWebClient,
                uri,
                null,
                TmfClientCommonHeaderUtil.prepareHeaderConsumer("token", getTokenService()),
                this::mockHandleError,
                ErrorMessage.class,
                shClientProperties));
  }

  @Test
  void test_patchRequest_withNoJsonPatch_shouldReturnNPE() {
    var clientConfig = configurations.getTmfClients().get(APIX_TMF_CLIENTS);
    var uri = buildUriWithId(clientConfig, "2");
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    patchRequest(
                        shWebClient,
                        uri,
                        null,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        ErrorMessage.class,
                        shClientProperties));

    StepVerifier.create(response).expectError(NullPointerException.class).verify();
  }

  @Test
  void test_getRequest_withEmptyAccessToken_shouldReturnNPE() {
    var clientConfig = configurations.getTmfClients().get(APIX_TMF_CLIENTS);
    var uri = buildUriWithId(clientConfig, "2");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            getRequest(
                shWebClient,
                uri,
                prepareHeaderConsumer(null, null),
                this::mockHandleError,
                ErrorMessage.class,
                shClientProperties));
  }

  @Test
  void test_getAllRequest_withEmptyAccessToken_shouldReturnNPE() {
    var clientConfig = configurations.getTmfClients().get(APIX_TMF_CLIENTS);
    var uri = buildUriWithId(clientConfig, "2");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () ->
            getAllRequest(
                shWebClient,
                uri,
                prepareHeaderConsumer("", null),
                this::mockHandleError,
                ErrorMessage.class,
                shClientProperties));
  }

  @Test
  void test_postRequest_withValidData_shouldReturnSuccess() {
    ObjectNode node = getTestData();
    var uri = createURI(SH_TMF_CLIENTS);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    postRequest(
                        shWebClient,
                        uri,
                        node,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        TestResponseModel.class,
                        this::mockHandleError,
                        shClientProperties));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertNotNull(testResponseModel.getId());
            })
        .verifyComplete();
  }

  @Test
  void test_postRequest_withValidDataHeaders_shouldReturnSuccess() {
    ObjectNode node = getTestData();
    var uri = createURI(SH_TMF_CLIENTS);
    MockServerUtils.setUpDynamicPostCallback(
        TMF_PATH, Headers.headers(Header.header("header1", "value1", "value2")));
    TmfRequestContext context =
        TmfRequestContext.builder().withHeaderValues("header1", "value1", "value2").build();
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    postRequest(
                        shWebClient,
                        uri,
                        node,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, context, getTokenService()),
                        TestResponseModel.class,
                        this::mockHandleError,
                        shClientProperties));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertNotNull(testResponseModel.getId());
            })
        .verifyComplete();
  }

  @Test
  void test_getRequest_withValidData_shouldReturnSuccess() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    getRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertNotNull(testResponseModel.getId());
            })
        .verifyComplete();
  }

  @Test
  void test_getRequest_withValidDataAndFilterSection_shouldReturnSuccess() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    getRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        TmfRequestContext.builder().withFields("id").build()));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertNotNull(testResponseModel.getId());
            })
        .verifyComplete();
  }

  @Test
  void test_getRequest_with1_shouldReturnSuccess() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var uri = createURI(SH_TMF_CLIENTS, id);
    MockServerUtils.setUpDynamicPostCallback(
        TMF_PATH, Headers.headers(Header.header("header1", "value1", "value2")));
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    getRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        TmfRequestContext.builder()
                            .withFields("id")
                            .withHeaderValues("header1", "value1", "value2")
                            .build()));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertNotNull(testResponseModel.getId());
            })
        .verifyComplete();
  }

  @Test
  void test_getRequest_withValidDataAndJsonFilterWithClientFilter_shouldReturnSuccess() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    getRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        String.class,
                        shClientProperties,
                        TmfRequestContext.builder().withClientJsonFilter("$.id").build()));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertEquals(id, testResponseModel);
            })
        .verifyComplete();
  }

  @Test
  void test_getRequest_withValidDataAndJsonFilterWithServerFilter_shouldReturnSuccess() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    getRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        TmfRequestContext.builder().withServerJsonFilter("$.id").build()));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertNotNull(testResponseModel.getId());
            })
        .verifyComplete();
  }

  @Test
  void test_getRequest_withValidData_shouldReturnStringPayload() {
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    getRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        String.class,
                        shClientProperties));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertInstanceOf(String.class, testResponseModel);
            })
        .verifyComplete();
  }

  @Test
  void test_patchRequest_withValidData_shouldReturnSuccess() throws JsonPointerException {
    MockServerUtils.setUpDynamicJsonPatchCallback(TMF_PATH);
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var patch =
        new JsonPatch(
            List.of(new ReplaceOperation(new JsonPointer("/name"), new TextNode("test_patch"))));

    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    patchRequest(
                        shWebClient,
                        uri,
                        patch,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties));

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
  void test_patchRequest_withHeaderValue_shouldReturnSuccess() throws JsonPointerException {
    MockServerUtils.setUpDynamicJsonPatchCallback(TMF_PATH, Headers.headers(Header.header("ifMatch", "true")));
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var patch =
        new JsonPatch(
            List.of(new ReplaceOperation(new JsonPointer("/name"), new TextNode("test_patch"))));
    TmfRequestContext context =
        TmfRequestContext.builder().withHeaderValues("ifMatch", "true").build();

    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    patchRequest(
                        shWebClient,
                        uri,
                        patch,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, context, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties));

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
  void test_patchRequest_withMergePatch_shouldReturnSuccess() {
    MockServerUtils.setUpDynamicMergePatchCallback(TMF_PATH);
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);
    var object = jsonToObject("{\"name\":\"test_patch\"}", TestResponseModel.class);

    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    patchRequest(
                        shWebClient,
                        uri,
                        object,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties));

    StepVerifier.create(response)
        .assertNext(
            testResponseModel -> {
              assertNotNull(testResponseModel);
              assertEquals("test_patch", testResponseModel.getName());
            })
        .verifyComplete();
  }

  @Test
  void test_deleteRequest_withValidData_shouldReturnSuccess() {
    MockServerUtils.setUpDynamicDeleteCallback(TMF_PATH);
    ObjectNode node = getTestData();
    String id = addDataToMockServerCache(TMF_PATH, node);

    var uri = createURI(SH_TMF_CLIENTS, id);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    deleteRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        shClientProperties));

    StepVerifier.create(response).verifyComplete();
  }

  @Test
  void test_getAllRequest_withServerLimit_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    getAllRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties));

    StepVerifier.create(response).expectNextCount(10).verifyComplete();
  }

  @Test
  void test_retrieveAllPages_withServerLimit_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties));

    StepVerifier.create(response).expectNextCount(40).verifyComplete();
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequest_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    Pageable pageable = TmfOffsetRequest.of(0, 5);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    StepVerifier.create(response).expectNextCount(40).verifyComplete();
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndSettingOffset_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    Pageable pageable = TmfOffsetRequest.of(25, 5);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    StepVerifier.create(response).expectNextCount(15).verifyComplete();
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndSettingDescSort_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    Pageable pageable = TmfOffsetRequest.of(Sort.Direction.DESC, "randomNumber");

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    List<TestResponseModel> results = new ArrayList<>();
    StepVerifier.create(response).recordWith(() -> results).expectNextCount(40).verifyComplete();

    int previousValue = results.get(0).getRandomNumber();
    for (int i = 1; i < results.size(); i++) {
      int currentValue = results.get(i).getRandomNumber();
      assertTrue(previousValue >= currentValue);
      previousValue = currentValue;
    }
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndSettingAscSort_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    Pageable pageable = TmfOffsetRequest.of(Sort.Direction.ASC, "randomNumber");

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    List<TestResponseModel> results = new ArrayList<>();
    StepVerifier.create(response).recordWith(() -> results).expectNextCount(40).verifyComplete();

    int previousValue = results.get(0).getRandomNumber();
    for (int i = 1; i < results.size(); i++) {
      int currentValue = results.get(i).getRandomNumber();
      assertTrue(previousValue <= currentValue);
      previousValue = currentValue;
    }
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndSettingMultipleSort_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    Pageable pageable = TmfOffsetRequest.of(Sort.Direction.ASC, "randomNumber", "even");

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    StepVerifier.create(response).expectNextCount(40).verifyComplete();
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndSettingServerFilter_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    String filter = "$[?(@.even == true)]";
    Pageable pageable = TmfOffsetRequest.of(0, 10).withServerFilter(filter);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    List<TestResponseModel> results = new ArrayList<>();
    StepVerifier.create(response).recordWith(() -> results).expectNextCount(20).verifyComplete();
    assertTrue(results.stream().allMatch(TestResponseModel::isEven));
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndSettingClientFilter_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    String filter = "$[?(@.even == true)].characteristics[*]";
    Pageable pageable = TmfOffsetRequest.of(0, 10).withClientFilter(filter);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.Characteristic.class,
                        shClientProperties,
                        pageable));

    List<TestResponseModel.Characteristic> list = new ArrayList<>();
    StepVerifier.create(response).recordWith(() -> list).expectNextCount(20).verifyComplete();

    assertNotNull(list);
    list.forEach(
        characteristic -> {
          assertNotNull(characteristic);
          assertNotNull(characteristic.getKey());
          assertNotNull(characteristic.getValue());
        });
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndFields_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    String fields = "id";
    Pageable pageable = TmfOffsetRequest.of(0, 10).withFields(fields);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    List<TestResponseModel> results = new ArrayList<>();
    StepVerifier.create(response).recordWith(() -> results).expectNextCount(40).verifyComplete();
    results.forEach(
        testResponseModel -> {
          assertNotNull(testResponseModel.getId());
          assertNull(testResponseModel.getDescription());
          assertNull(testResponseModel.getName());
          assertNull(testResponseModel.getState());
          assertEquals(0, testResponseModel.getRandomNumber());
          assertEquals(0, testResponseModel.getOrderNumber());
          assertFalse(testResponseModel.isEven());
          assertNull(testResponseModel.getCreatedBy());
          assertNull(testResponseModel.getCreatedDate());
          assertNull(testResponseModel.getUpdatedBy());
          assertNull(testResponseModel.getUpdatedDate());
        });
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndEmptyStringFields_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    String fields = "";
    Pageable pageable = TmfOffsetRequest.of(0, 10).withFields(fields);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    List<TestResponseModel> results = new ArrayList<>();
    StepVerifier.create(response).recordWith(() -> results).expectNextCount(40).verifyComplete();
    results.forEach(
        testResponseModel -> {
          assertNotNull(testResponseModel.getId());
          assertNotNull(testResponseModel.getDescription());
          assertNotNull(testResponseModel.getName());
          assertNotNull(testResponseModel.getState());
          assertNotNull(testResponseModel.getCreatedBy());
          assertNotNull(testResponseModel.getCreatedDate());
        });
  }

  @Test
  void test_retrieveAllPages_withTmfOffsetRequestAndEmptyFields_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);
    String fields = "";
    Pageable pageable = TmfOffsetRequest.of(0, 10).withFields(fields);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveAllPages(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    List<TestResponseModel> results = new ArrayList<>();
    StepVerifier.create(response).recordWith(() -> results).expectNextCount(40).verifyComplete();
    results.forEach(
        testResponseModel -> {
          assertNotNull(testResponseModel.getId());
          assertNotNull(testResponseModel.getDescription());
          assertNotNull(testResponseModel.getName());
          assertNotNull(testResponseModel.getState());
          assertNotNull(testResponseModel.getCreatedBy());
          assertNotNull(testResponseModel.getCreatedDate());
        });
  }

  @Test
  void test_retrieveSinglePage_withServerLimit_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 40);
    var uri = createURI(SH_TMF_CLIENTS);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveSinglePageWithPageResponse(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        TmfOffsetRequest.of(0)));

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
  void test_retrieveSinglePage_withServerLimitAndNoData_shouldReturnSuccess() {
    var uri = createURI(SH_TMF_CLIENTS);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveSinglePageWithPageResponse(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        TmfOffsetRequest.of(0)));

    StepVerifier.create(response)
        .assertNext(
            tmfPage -> {
              assertNotNull(tmfPage);
              assertEquals(0, tmfPage.getSize());
              assertEquals(0, tmfPage.getTotalPages());
              assertEquals(0, tmfPage.getTotalElements());
              assertEquals(0, tmfPage.getNumber());
              assertFalse(tmfPage.hasNext());
              assertTrue(tmfPage.isLast());
              assertNotNull(tmfPage.getContent());
            })
        .verifyComplete();
  }

  @Test
  void test_retrieveSinglePage_withOffsetRequest_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 35);
    var uri = createURI(SH_TMF_CLIENTS);
    Pageable pageable = TmfOffsetRequest.of(30, 10);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveSinglePageWithPageResponse(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    StepVerifier.create(response)
        .assertNext(
            tmfPage -> {
              assertNotNull(tmfPage);
              assertEquals(5, tmfPage.getSize());
              assertEquals(4, tmfPage.getTotalPages());
              assertEquals(35, tmfPage.getTotalElements());
              assertEquals(3, tmfPage.getNumber());
              assertFalse(tmfPage.hasNext());
              assertTrue(tmfPage.isLast());
              assertNotNull(tmfPage.getContent());
            })
        .verifyComplete();
  }

  @Test
  void test_retrieveSinglePage_withPageRequest_shouldReturnSuccess() {
    addingMockServerData(TMF_PATH, 35);
    var uri = createURI(SH_TMF_CLIENTS);
    Pageable pageable = PageRequest.of(3, 10);

    var response =
        shTokenService
            .getToken()
            .flatMapMany(
                token ->
                    retrieveSinglePageWithPageResponse(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, pageable, getTokenService()),
                        this::mockHandleError,
                        TestResponseModel.class,
                        shClientProperties,
                        pageable));

    StepVerifier.create(response)
        .assertNext(
            tmfPage -> {
              assertNotNull(tmfPage);
              assertEquals(5, tmfPage.getSize());
              assertEquals(4, tmfPage.getTotalPages());
              assertEquals(35, tmfPage.getTotalElements());
              assertEquals(3, tmfPage.getNumber());
              assertFalse(tmfPage.hasNext());
              assertTrue(tmfPage.isLast());
              assertNotNull(tmfPage.getContent());
            })
        .verifyComplete();
  }

  @Test
  void test_deleteRequest_withInvalidData_shouldThrowTmfException() {
    configurations.getTmfClients().get("dxl-tmf-x-client").setBaseUrl(MockServerUtils.BASE_URL);
    var clientConfig = configurations.getTmfClients().get("dxl-tmf-x-client");
    var uri = buildUriWithId(clientConfig, "2");
    assertThrows(
        IllegalArgumentException.class,
        () -> deleteRequest(shWebClient, uri, null, this::mockHandleError, shClientProperties));
  }

  @Test
  void test_deleteRequest_withValidData_shouldReturnOkay() {
    configurations.getTmfClients().get("dxl-tmf-x-client").setBaseUrl(MockServerUtils.BASE_URL);
    var clientConfig = configurations.getTmfClients().get("dxl-tmf-x-client");
    var uri = buildUriWithId(clientConfig, "2");
    MockServerUtils.expectDeleteMethod("deleteX", HttpStatus.OK);
    var response =
        shTokenService
            .getToken()
            .flatMap(
                token ->
                    deleteRequest(
                        shWebClient,
                        uri,
                        TmfClientCommonHeaderUtil.prepareHeaderConsumer(token, getTokenService()),
                        this::mockHandleError,
                        shClientProperties));
    StepVerifier.create(response).expectError(TmfClientException.class).verify();
  }

  private URI createURI(String client) {
    var clientConfig = configurations.getTmfClients().get(client);
    return buildUri(clientConfig);
  }

  private URI createURI(String client, String id) {
    var clientConfig = configurations.getTmfClients().get(client);
    return buildUriWithId(clientConfig, id);
  }

  private void addingMockServerData(String path, int count) {
    List<String> example = List.of("test1", "test2", "test3", "test4", "test5");
    for (int i = 0; i < count; i++) {
      ObjectNode node = getTestData();
      node.put("orderNumber", i);
      node.put("even", i % 2 == 0);
      ArrayNode arrayNode = JacksonUtil.getDefaultObjectMapper().createArrayNode();
      ObjectNode nodeCharacteristic = JacksonUtil.getDefaultObjectMapper().createObjectNode();
      nodeCharacteristic.put("key", "name");
      nodeCharacteristic.put("value", example.get(i % 5));
      arrayNode.add(nodeCharacteristic);
      node.set("characteristics", arrayNode);
      addDataToMockServerCache(path, node);
    }
  }

  private ObjectNode getTestData() {
    ObjectNode node = JacksonUtil.getDefaultObjectMapper().createObjectNode();
    node.put("description", "test_getAll");
    node.put("name", "test");
    node.put("randomNumber", new Random().nextInt(5));
    return node;
  }

  private String addDataToMockServerCache(String path, ObjectNode json) {
    HttpRequest httpRequest =
        new HttpRequest().withPath(path).withBody(JacksonUtil.objectToJson(json));
    DynamicPostCallback dynamicPostCallback = new DynamicPostCallback();
    HttpResponse response = dynamicPostCallback.handle(httpRequest);
    assertEquals(200, response.getStatusCode());
    JsonNode responseJson = JacksonUtil.jsonToTree(response.getBodyAsString());
    assertNotNull(responseJson.get("id").asText());
    return responseJson.get("id").asText();
  }

  private URI getCallbackUri() {
    return URI.create(MockServerUtils.BASE_URL + "/listener/statechangeEventType");
  }

  private Mono<? extends Throwable> mockHandleError(ClientResponse clientResponse) {
    var httpStatus = clientResponse.statusCode();
    return clientResponse
        .bodyToMono(ErrorMessage.class)
        .onErrorResume(throwable -> Mono.empty())
        .switchIfEmpty(Mono.defer(() -> Mono.error(new TmfClientException(httpStatus))))
        .map(errorMessage -> new TmfClientException(httpStatus, errorMessage));
  }

  private TokenService getTokenService() {
    return new OpenidTokenServiceMockImpl();
  }
}
