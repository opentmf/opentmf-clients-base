package org.opentmf.common.util;

import java.util.Map;
import java.util.function.Consumer;
import org.opentmf.client.common.service.api.TokenService;
import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;
import org.opentmf.common.model.TmfClientCommonsConstants;
import org.opentmf.common.model.TmfOffsetRequest;
import org.opentmf.common.model.TmfRequestContext;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public class TmfClientCommonHeaderUtil {
  private TmfClientCommonHeaderUtil() {}

  public static Consumer<HttpHeaders> prepareHeaderConsumer(
      String authToken, TokenService tokenService) {
    validateAuthToken(authToken);
    return setAuthorization(authToken, tokenService);
  }

  public static Consumer<HttpHeaders> prepareHeaderConsumer(
      String authToken, TmfClientConfig config, TokenService tokenService) {
    validateAuthToken(authToken);
    return combineConsumers(
        setAuthorization(authToken, tokenService), addTmfConfigFixedHeaders(config));
  }

  public static Consumer<HttpHeaders> prepareHeaderConsumer(
      String authToken, Pageable pageQuery, TokenService tokenService) {
    if (pageQuery instanceof TmfOffsetRequest tmfOffsetRequest) {
      return prepareHeaderConsumer(authToken, tmfOffsetRequest.getRequestContext(), tokenService);
    }
    return prepareHeaderConsumer(authToken, tokenService);
  }

  public static Consumer<HttpHeaders> prepareHeaderConsumer(
      String authToken, TmfClientConfig config, Pageable pageQuery, TokenService tokenService) {
    if (pageQuery instanceof TmfOffsetRequest tmfOffsetRequest) {
      return prepareHeaderConsumer(
          authToken, config, tmfOffsetRequest.getRequestContext(), tokenService);
    }
    return prepareHeaderConsumer(authToken, config, tokenService);
  }

  public static Consumer<HttpHeaders> prepareHeaderConsumer(
      String authToken, TmfRequestContext requestContext, TokenService tokenService) {
    validateAuthToken(authToken);
    return combineConsumers(
        setAuthorization(authToken, tokenService), addRequestContextHeaders(requestContext));
  }

  public static Consumer<HttpHeaders> prepareHeaderConsumer(
      String authToken,
      TmfClientConfig config,
      TmfRequestContext requestContext,
      TokenService tokenService) {
    validateAuthToken(authToken);
    return combineConsumers(
        setAuthorization(authToken, tokenService),
        addRequestContextHeaders(requestContext),
        addTmfConfigFixedHeaders(config));
  }

  private static Consumer<HttpHeaders> setAuthorization(
      String authToken, TokenService tokenService) {
    return httpHeaders ->
        httpHeaders.set(HttpHeaders.AUTHORIZATION, tokenService.getTokenType() + " " + authToken);
  }

  private static Consumer<HttpHeaders> addRequestContextHeaders(
      TmfRequestContext requestContext) {
    return (HttpHeaders httpHeaders) -> {
      if (requestContext != null) {
        addHeaders(httpHeaders, requestContext.getHeaderParameters());
      }
    };
  }

  private static Consumer<HttpHeaders> addTmfConfigFixedHeaders(TmfClientConfig tmfClientConfig) {
    return (HttpHeaders httpHeaders) -> {
      if (tmfClientConfig != null) {
        addHeaders(httpHeaders, tmfClientConfig.getFixedHeaders());
      }
    };
  }

  private static void addHeaders(HttpHeaders httpHeaders, Map<String, String> headers) {
    if (headers != null && !headers.isEmpty()) {
      headers.forEach(httpHeaders::add);
    }
  }

  private static void addHeaders(HttpHeaders httpHeaders, MultiValueMap<String, String> headers) {
    if (headers != null && !headers.isEmpty()) {
      headers.forEach((key, values) -> {
        for (String value : values) {
          httpHeaders.add(key, value);
        }
      });
    }
  }

  private static void validateAuthToken(String authToken) {
    if (!StringUtils.hasText(authToken)) {
      throw new IllegalArgumentException(TmfClientCommonsConstants.ERROR_MSG_EMPTY_AUTH_TOKEN);
    }
  }

  public static String getHeadersFirstValue(String key, ResponseEntity<?> responseEntity) {
    var headers = responseEntity.getHeaders();
    if (headers.containsKey(key)) {
      var value = headers.getFirst(key);
      return value == null ? "" : value.trim();
    }
    return null;
  }

  @SafeVarargs
  private static Consumer<HttpHeaders> combineConsumers(Consumer<HttpHeaders>... consumers) {
    return (HttpHeaders httpHeaders) -> {
      for (Consumer<HttpHeaders> consumer : consumers) {
        consumer.accept(httpHeaders);
      }
    };
  }
}
