package com.pia.tmf.common.util;

import static com.pia.client.common.util.TokenUtil.TOKEN_TYPE_BEARER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pia.client.common.service.api.TokenService;
import com.pia.tmf.common.config.TmfClientConfigurations;
import com.pia.tmf.common.model.TmfOffsetRequest;
import com.pia.tmf.common.model.TmfRequestContext;
import java.util.HashMap;
import java.util.function.Consumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Mono;

class TmfClientCommonHeaderUtilTest {
  @Test
  void testPrepareHeaderConsumerWithAuthToken() {
    String authToken = "testToken";
    Consumer<HttpHeaders> consumer = TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken,
        getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    String first = httpHeaders.getFirst(HttpHeaders.AUTHORIZATION);
    Assertions.assertNotNull(first);
    assertEquals(authToken, first.substring(7));
  }

  private TokenService getTokenService() {
    return new TokenService() {
      @Override
      public String getTokenType() {
        return TOKEN_TYPE_BEARER;
      }

      @Override
      public Mono<String> getToken() {
        return Mono.just("mock_token");
      }

      @Override
      public Mono<String> getToken(String s) {
        return Mono.just("mock_token");
      }
    };
  }

  @Test
  void testPrepareHeaderConsumerWithAuthTokenAndConfig() {
    String authToken = "testToken";
    TmfClientConfigurations.TmfClientConfig config =
        mock(TmfClientConfigurations.TmfClientConfig.class);
    when(config.getFixedHeaders()).thenReturn(new HashMap<>());
    Consumer<HttpHeaders> consumer =
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken, config, getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals(authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION).substring(7));
  }

  @Test
  void testPrepareHeaderConsumerWithAuthTokenAndPageQuery() {
    String authToken = "testToken";
    Pageable pageQuery = mock(TmfOffsetRequest.class);
    when(((TmfOffsetRequest) pageQuery).getRequestContext()).thenReturn(null);
    Consumer<HttpHeaders> consumer =
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken, pageQuery, getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals(authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION).substring(7));
  }

  @Test
  void testPrepareHeaderConsumerWithAuthTokenAndConfigAndPageQuery() {
    String authToken = "testToken";
    TmfClientConfigurations.TmfClientConfig config =
        mock(TmfClientConfigurations.TmfClientConfig.class);
    when(config.getFixedHeaders()).thenReturn(new HashMap<>());
    Pageable pageQuery = mock(TmfOffsetRequest.class);
    when(((TmfOffsetRequest) pageQuery).getRequestContext()).thenReturn(null);
    Consumer<HttpHeaders> consumer =
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken, config, pageQuery,
            getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals(authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION).substring(7));
  }

  @Test
  void testPrepareHeaderConsumerWithAuthTokenAndRetrievalContext() {
    String authToken = "testToken";
    TmfRequestContext requestContext = mock(TmfRequestContext.class);
    when(requestContext.getHeaderParameters()).thenReturn(new LinkedMultiValueMap<>());
    Consumer<HttpHeaders> consumer =
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken, requestContext,
            getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals(authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION).substring(7));
  }

  @Test
  void testPrepareHeaderConsumerWithAuthTokenAndConfigAndRetrievalContext() {
    String authToken = "testToken";
    TmfClientConfigurations.TmfClientConfig config =
        mock(TmfClientConfigurations.TmfClientConfig.class);
    when(config.getFixedHeaders()).thenReturn(new HashMap<>());
    TmfRequestContext requestContext = mock(TmfRequestContext.class);
    when(requestContext.getHeaderParameters()).thenReturn(new LinkedMultiValueMap<>());
    Consumer<HttpHeaders> consumer =
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken, config, requestContext,
            getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals(authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION).substring(7));
  }

  @Test
  void testGetHeadersFirstValue() {
    String key = "testKey";
    String value = "testValue";
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(key, value);
    String result =
        TmfClientCommonHeaderUtil.getHeadersFirstValue(
            key, new ResponseEntity<>(httpHeaders, HttpStatusCode.valueOf(200)));
    assertEquals(value, result);
  }

  @Test
  void testGetHeadersConsumerWithAuthToken() {
    String authToken = "testToken";
    Consumer<HttpHeaders> consumer = TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken,
        getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals("Bearer " + authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
  }

  @Test
  void testGetHeadersConsumerWithAuthTokenAndPageQuery() {
    String authToken = "testToken";
    Pageable pageQuery = mock(TmfOffsetRequest.class);
    when(((TmfOffsetRequest) pageQuery).getRequestContext()).thenReturn(null);
    Consumer<HttpHeaders> consumer =
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken, pageQuery, getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals("Bearer " + authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
  }

  @Test
  void testGetHeadersConsumerWithAuthTokenAndRetrievalContext() {
    String authToken = "testToken";
    TmfRequestContext requestContext = mock(TmfRequestContext.class);
    when(requestContext.getHeaderParameters()).thenReturn(new LinkedMultiValueMap<>());
    Consumer<HttpHeaders> consumer =
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken, requestContext,
            getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals("Bearer " + authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
  }

  @Test
  void testGetHeadersConsumerWithAuthTokenAndConfigAndRetrievalContext() {
    String authToken = "testToken";
    TmfClientConfigurations.TmfClientConfig config =
        mock(TmfClientConfigurations.TmfClientConfig.class);
    when(config.getFixedHeaders()).thenReturn(new HashMap<>());
    TmfRequestContext requestContext = mock(TmfRequestContext.class);
    when(requestContext.getHeaderParameters()).thenReturn(new LinkedMultiValueMap<>());
    Consumer<HttpHeaders> consumer = TmfClientCommonHeaderUtil.prepareHeaderConsumer(authToken,
        config, requestContext, getTokenService());
    HttpHeaders httpHeaders = new HttpHeaders();
    consumer.accept(httpHeaders);
    assertEquals("Bearer " + authToken, httpHeaders.getFirst(HttpHeaders.AUTHORIZATION));
  }

  @Test
  void testGetHeadersFirstValueWithNullResponseEntity() {
    String key = "testKey";
    assertThrows(
        NullPointerException.class,
        () -> TmfClientCommonHeaderUtil.getHeadersFirstValue(key, null));
  }

  @Test
  void testGetHeadersConsumerWithNullAuthToken() {
    assertThrows(
        IllegalArgumentException.class,
        () -> TmfClientCommonHeaderUtil.prepareHeaderConsumer(null, null));
  }

  @Test
  void testGetHeadersConsumerWithEmptyAuthToken() {
    var tokenService = getTokenService();
    assertThrows(IllegalArgumentException.class, () ->
        TmfClientCommonHeaderUtil.prepareHeaderConsumer("", tokenService));
  }
}
