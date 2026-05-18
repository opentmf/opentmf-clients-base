package org.opentmf.common.util;

import static org.opentmf.client.common.util.WebClientUtil.retry;
import static org.opentmf.common.util.ConverterUtil.toInt;
import static org.springframework.util.StringUtils.hasText;

import com.github.fge.jsonpatch.JsonPatch;
import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.opentmf.client.common.model.BaseClientProperties;
import org.opentmf.client.common.service.api.TokenService;
import org.opentmf.client.common.util.WebClientUtil;
import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;
import org.opentmf.common.exception.TmfClientException;
import org.opentmf.common.model.ErrorMessage;
import org.opentmf.common.model.JsonFilter;
import org.opentmf.common.model.OffsetPage;
import org.opentmf.common.model.Scope;
import org.opentmf.common.model.TmfClientCommonsConstants;
import org.opentmf.common.model.TmfOffsetRequest;
import org.opentmf.common.model.TmfPage;
import org.opentmf.common.model.TmfRequestContext;
import org.opentmf.commons.util.JacksonUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Utility class for common operations used in TMF (Telecom Management Forum) client applications.
 * This class provides methods for building URIs, making HTTP requests, handling pagination,
 * registering and unregistering event listeners, and other common functionalities.
 *
 * <p>author: Yusuf BOZKURT
 */
@Slf4j
public final class TmfClientCommonUtil {

  public static final String FILTER = "filter";
  public static final String FIELDS = "fields";

  /** Private constructor to prevent instantiation of this utility class. */
  private TmfClientCommonUtil() {}

  /**
   * Retrieves an authentication token for the specified client path from the given TokenService. If
   * the client path has a scope defined, the token is retrieved with that scope; otherwise, a token
   * is retrieved without a specific scope.
   *
   * @param tokenService The TokenService used to retrieve the token.
   * @return A Mono emitting the authentication token as a String.
   */
  public static Mono<String> getToken(
      Scope scopeKey, TmfClientConfig config, TokenService tokenService) {
    String scope = config.getScopes().get(scopeKey);
    return hasText(scope) ? tokenService.getToken(scope) : tokenService.getToken();
  }

  /**
   * Builds a URI based on the provided TMF client configuration and client path.
   *
   * @param config The TMF client configuration containing base URL and context path.
   * @return The constructed URI.
   */
  public static URI buildUri(TmfClientConfig config) {

    return UriComponentsBuilder.fromUriString(config.getBaseUrl())
        .path(config.getContextPath())
        .path(config.getEndpoint())
        .build()
        .toUri();
  }

  /**
   * Builds a URI with an ID parameter based on the provided TMF client configuration, client path,
   * and ID value.
   *
   * @param config The TMF client configuration containing base URL and context path.
   * @param id The ID value to be included in the URI.
   * @return The constructed URI.
   */
  public static URI buildUriWithId(TmfClientConfig config, String id) {
    Objects.requireNonNull(id, TmfClientCommonsConstants.ERROR_MSG_ID_RESPONSE);
    return UriComponentsBuilder.fromUriString(config.getBaseUrl())
        .path(config.getContextPath())
        .path(config.getEndpoint())
        .path("/{id}")
        .build(id);
  }

  /**
   * Builds a URI with an ID parameter and additional filter parameters based on the provided TMF
   * client configuration, client path, ID value, and filter.
   *
   * @param config The TMF client configuration containing base URL and context path.
   * @param id The ID value to be included in the URI.
   * @param requestContext The filter object containing filter parameters.
   * @return The constructed URI.
   */
  public static URI buildUriWithId(
      TmfClientConfig config, String id, TmfRequestContext requestContext) {
    Objects.requireNonNull(id, TmfClientCommonsConstants.ERROR_MSG_ID_RESPONSE);
    var uriComponentsBuilder =
        UriComponentsBuilder.fromUriString(config.getBaseUrl())
            .path(config.getContextPath())
            .path(config.getEndpoint())
            .queryParams(requestContext != null ? requestContext.getQueryParameters() : null)
            .path("/{id}");

    if (requestContext != null) {
      if (requestContext.getJsonFilterType() == JsonFilter.TYPE.SERVER) {
        uriComponentsBuilder.queryParam(FILTER, requestContext.getJsonFilterQuery());
      }

      if (requestContext.getFields() != null && !requestContext.getFields().isEmpty()) {
        uriComponentsBuilder.queryParam(FIELDS, prepareFieldsQuery(requestContext.getFields()));
      }
    }

    return uriComponentsBuilder.build(id);
  }

  /**
   * Builds a URI with query parameters based on the provided TMF client configuration, client path,
   * and query parameters.
   *
   * @param config The TMF client configuration containing base URL and context path.
   * @param queryParams The query parameters to be included in the URI.
   * @return The constructed URI.
   */
  public static URI buildUri(TmfClientConfig config, MultiValueMap<String, String> queryParams) {
    return UriComponentsBuilder.fromUriString(config.getBaseUrl())
        .path(config.getContextPath())
        .path(config.getEndpoint())
        .queryParams(queryParams)
        .build()
        .toUri();
  }

  public static URI buildUri(TmfClientConfig config, TmfRequestContext requestContext) {
    return UriComponentsBuilder.fromUriString(config.getBaseUrl())
        .path(config.getContextPath())
        .path(config.getEndpoint())
        .queryParams(requestContext != null ? requestContext.getQueryParameters() : null)
        .build()
        .toUri();
  }

  private static URI updateUri(URI uri, TmfRequestContext requestContext) {
    var qs = new StringBuilder();
    if (requestContext.getJsonFilterType() == JsonFilter.TYPE.SERVER) {
      appendQueryParam(qs, FILTER, requestContext.getJsonFilterQuery());
    }
    if (requestContext.getFields() != null && !requestContext.getFields().isEmpty()) {
      appendQueryParam(qs, FIELDS, prepareFieldsQuery(requestContext.getFields()));
    }
    return appendQueryString(uri, qs);
  }

  private static URI updateUri(URI uri, Pageable pageable) {
    var qs = new StringBuilder();
    appendQueryParam(qs, "offset", String.valueOf(pageable.getOffset()));
    appendQueryParam(qs, "limit", String.valueOf(pageable.getPageSize()));
    if (!pageable.getSort().isEmpty()) {
      appendQueryParam(qs, "sort", sortStringQuery(pageable.getSort()));
    }

    if (pageable instanceof TmfOffsetRequest tmfOffsetRequest) {
      if (tmfOffsetRequest.getJsonFilterTYpe() == JsonFilter.TYPE.SERVER) {
        appendQueryParam(qs, FILTER, tmfOffsetRequest.getJsonFilterQuery());
      }
      if (tmfOffsetRequest.getFields() != null && !tmfOffsetRequest.getFields().isEmpty()) {
        appendQueryParam(qs, FIELDS, prepareFieldsQuery(tmfOffsetRequest.getFields()));
      }
      if (tmfOffsetRequest.getQueryParameters() != null
          && !tmfOffsetRequest.getQueryParameters().isEmpty()) {
        tmfOffsetRequest.getQueryParameters().forEach((name, values) -> {
          if (values != null) {
            values.forEach(value -> appendQueryParam(qs, name, value));
          }
        });
      }
    }
    return appendQueryString(uri, qs);
  }

  // Appends a query string to a URI by string concatenation, preserving the original URI's
  // percent-encoded path. Avoids UriComponentsBuilder.fromUri(uri).build().toUri() which would
  // re-encode an already-encoded path (e.g. composite-key ids like Spec:(version=1)).
  private static URI appendQueryString(URI uri, StringBuilder queryString) {
    if (queryString.length() == 0) {
      return uri;
    }
    String s = uri.toString();
    char sep = s.indexOf('?') >= 0 ? '&' : '?';
    return URI.create(s + sep + queryString);
  }

  private static void appendQueryParam(StringBuilder sb, String name, String value) {
    if (sb.length() > 0) {
      sb.append('&');
    }
    sb.append(name).append('=').append(UriUtils.encodeQueryParam(value, StandardCharsets.UTF_8));
  }

  private static String sortStringQuery(Sort sort) {
    return sort.stream()
        .map(
            order -> (order.getDirection() == Sort.Direction.DESC ? "-" : "") + order.getProperty())
        .reduce((a, b) -> a + "," + b)
        .orElse("");
  }

  private static String prepareFieldsQuery(Set<String> fields) {
    return fields.stream().reduce((a, b) -> a + "," + b).orElse("");
  }

  /**
   * Executes a POST request to the specified URI with the provided body and access token, handling
   * errors and retries as per the provided properties.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the POST request is made.
   * @param body The request body.
   * @param headersConsumer The authentication token used for authorization.
   * @param t The class type of the expected response.
   * @param errorhandler Function to handle errors.
   * @param properties The properties governing the behavior of the request.
   * @return A Mono emitting the response object.
   */
  public static <T> Mono<T> postRequest(
      WebClient webClient,
      URI uri,
      Object body,
      Consumer<HttpHeaders> headersConsumer,
      Class<T> t,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      BaseClientProperties properties) {
    Objects.requireNonNull(
        body, TmfClientCommonsConstants.ERROR_MSG_EMPTY_RESPONSE.formatted(t.getSimpleName()));
    var headers = prepareAndValidateHeaders(headersConsumer, MediaType.APPLICATION_JSON);
    return webClient
        .post()
        .uri(uri)
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .bodyValue(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, errorhandler)
        .bodyToMono(t)
        .retryWhen(
            retry(properties.getNumRetries(), Duration.ofMillis(properties.getRetryWaitMillis())));
  }

  /**
   * Executes a PATCH request to the specified URI with the provided JSON patch and access token,
   * handling errors and retries as per the provided properties.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the PATCH request is made.
   * @param jsonPatch The JSON patch to apply.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param t The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param <T> The type of the expected response.
   * @return A Mono emitting the response object.
   * @throws NullPointerException if any of the required parameters is null.
   */
  public static <T> Mono<T> patchRequest(
      WebClient webClient,
      URI uri,
      JsonPatch jsonPatch,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties) {
    Objects.requireNonNull(
        jsonPatch, TmfClientCommonsConstants.ERROR_MSG_EMPTY_RESPONSE.formatted(t.getSimpleName()));
    var headers =
        prepareAndValidatePatchHeaders(headersConsumer, TmfClientCommonsConstants.MEDIA_TYPE_JSON_PATCH);
    return webClient
        .patch()
        .uri(uri)
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .bodyValue(jsonPatch)
        .retrieve()
        .onStatus(HttpStatusCode::isError, errorhandler)
        .bodyToMono(t)
        .retryWhen(
            WebClientUtil.retry(
                properties.getNumRetries(), Duration.ofMillis(properties.getRetryWaitMillis())));
  }

  /**
   * Executes a collection-level PATCH request (JSON Patch on the collection root) to the specified
   * URI with the provided JSON patch and access token, expecting a JSON array response. This
   * supports the TMF v4 high-performance bulk-creation pattern where the request body is an RFC
   * 6902 JSON Patch carrying a sequence of {@code add} operations on path {@code "/"}, and the
   * response is a JSON array of created resources in the same order as the patch operations.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the PATCH request is made (collection root, no id segment).
   * @param jsonPatch The JSON patch to apply.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param t The class type of each element in the expected response array.
   * @param properties The properties governing the behavior of the request.
   * @param <T> The type of each element in the expected response array.
   * @return A Mono emitting the list of response objects.
   * @throws NullPointerException if any of the required parameters is null.
   */
  public static <T> Mono<List<T>> patchRequestList(
      WebClient webClient,
      URI uri,
      JsonPatch jsonPatch,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties) {
    Objects.requireNonNull(
        jsonPatch, TmfClientCommonsConstants.ERROR_MSG_EMPTY_RESPONSE.formatted(t.getSimpleName()));
    var headers =
        prepareAndValidatePatchHeaders(headersConsumer, TmfClientCommonsConstants.MEDIA_TYPE_JSON_PATCH);
    return webClient
        .patch()
        .uri(uri)
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .bodyValue(jsonPatch)
        .retrieve()
        .onStatus(HttpStatusCode::isError, errorhandler)
        .bodyToFlux(t)
        .collectList()
        .retryWhen(
            WebClientUtil.retry(
                properties.getNumRetries(), Duration.ofMillis(properties.getRetryWaitMillis())));
  }

  /**
   * Executes a PATCH request to the specified URI with the provided body and access token, handling
   * errors and retries as per the provided properties.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the PATCH request is made.
   * @param body The request body.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param t The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param <T> The type of the expected response.
   * @return A Mono emitting the response object.
   * @throws NullPointerException if any of the required parameters is null.
   */
  public static <T> Mono<T> patchRequest(
      WebClient webClient,
      URI uri,
      Object body,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties) {
    Objects.requireNonNull(
        body, TmfClientCommonsConstants.ERROR_MSG_EMPTY_RESPONSE.formatted(t.getSimpleName()));
    var headers =
        prepareAndValidatePatchHeaders(
            headersConsumer, TmfClientCommonsConstants.MEDIA_TYPE_MERGE_PATCH);
    return webClient
        .patch()
        .uri(uri)
        .headers(httpHeaders -> httpHeaders.addAll(headers))
        .bodyValue(body)
        .retrieve()
        .onStatus(HttpStatusCode::isError, errorhandler)
        .bodyToMono(t)
        .retryWhen(
            WebClientUtil.retry(
                properties.getNumRetries(), Duration.ofMillis(properties.getRetryWaitMillis())));
  }

  /**
   * Executes a GET request to the specified URI with the provided access token and filter, handling
   * errors and retries as per the provided properties. If a client-side filter is provided, it
   * processes the response accordingly.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the GET request is made.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param t The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param requestContext The filter to apply to the response.
   * @param <T> The type of the expected response.
   * @return A Mono emitting the response object.
   * @throws NullPointerException if any of the required parameters is null.
   */
  public static <T> Mono<T> getRequest(
      WebClient webClient,
      URI uri,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties,
      TmfRequestContext requestContext) {

    var updateUri = updateUri(uri, requestContext);
    if (requestContext.getJsonFilterType() == JsonFilter.TYPE.CLIENT) {
      return getRequestWithClientFilter(
          webClient, updateUri, headersConsumer, errorhandler, t, properties, requestContext);
    }
    return getRequest(webClient, updateUri, headersConsumer, errorhandler, t, properties);
  }

  public static <T> Mono<T> getRequest(
      WebClient webClient,
      URI uri,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties) {
    validateHeadersConsumer(headersConsumer);
    return webClient
        .get()
        .uri(uri)
        .headers(headersConsumer)
        .retrieve()
        .onStatus(HttpStatusCode::isError, errorhandler)
        .bodyToMono(t)
        .retryWhen(
            WebClientUtil.retry(
                properties.getNumRetries(), Duration.ofMillis(properties.getRetryWaitMillis())));
  }

  private static <T> Mono<T> getRequestWithClientFilter(
      WebClient webClient,
      URI uri,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties,
      TmfRequestContext requestContext) {
    return getRequest(webClient, uri, headersConsumer, errorhandler, Object.class, properties)
        .map(object -> JsonPath.read(object, requestContext.getJsonFilterQuery()));
  }

  /**
   * Executes a GET request to the specified URI with the provided access token, handling errors and
   * retries as per the provided properties.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the GET request is made.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param t The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param <T> The type of the expected response.
   * @return A Flux emitting the response objects.
   * @throws NullPointerException if any of the required parameters is null.
   */
  public static <T> Flux<T> getAllRequest(
      WebClient webClient,
      URI uri,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties) {
    return retrieveSinglePage(
        webClient, uri, headersConsumer, errorhandler, t, properties, TmfOffsetRequest.of(0));
  }

  /**
   * Retrieves all pages of data from the specified URI with the provided access token, handling
   * errors and retries as per the provided properties.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param url The base URL from which data is retrieved.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param type The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param <T> The type of the expected response.
   * @return A Flux emitting the response objects.
   */
  public static <T> Flux<T> retrieveAllPages(
      WebClient webClient,
      URI url,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> type,
      BaseClientProperties properties) {
    return retrieveAllPages(
        webClient, url, headersConsumer, errorhandler, type, properties, TmfOffsetRequest.of());
  }

  /**
   * Retrieves all pages of data from the specified URI with the provided access token, handling
   * errors and retries as per the provided properties and applying the given page query.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param url The base URL from which data is retrieved.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param type The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param pageQuery The page query specifying pagination parameters.
   * @param <T> The type of the expected response.
   * @return A Flux emitting the response objects.
   */
  public static <T> Flux<T> retrieveAllPages(
      WebClient webClient,
      URI url,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> type,
      BaseClientProperties properties,
      Pageable pageQuery) {

    if (pageQuery instanceof TmfOffsetRequest tmfOffsetRequest
        && (tmfOffsetRequest.getJsonFilterTYpe() == JsonFilter.TYPE.CLIENT)) {
      return retrieveAllPagesWithClientFilter(
          webClient, url, headersConsumer, errorhandler, type, properties, tmfOffsetRequest);
    }

    return retrieveAllPagesWithServerFilter(
        webClient, url, headersConsumer, errorhandler, type, properties, pageQuery);
  }

  /**
   * Retrieves all pages of data from the specified URI with the provided access token, handling
   * errors and retries as per the provided properties, and applying client-side filtering.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param url The base URL from which data is retrieved.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param type The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param tmfOffsetRequest The offset request containing client-side filtering information.
   * @param <T> The type of the expected response.
   * @return A Flux emitting the response objects.
   */
  private static <T> Flux<T> retrieveAllPagesWithClientFilter(
      WebClient webClient,
      URI url,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> type,
      BaseClientProperties properties,
      TmfOffsetRequest tmfOffsetRequest) {

    return recursiveRetrieve(
            webClient,
            url,
            headersConsumer,
            errorhandler,
            Object.class,
            properties,
            tmfOffsetRequest)
        .collectList()
        .flatMapMany(
            (List<Object> objects) -> {
              List<T> result = JsonPath.read(objects, tmfOffsetRequest.getJsonFilter().getQuery());
              return Flux.fromIterable(
                  result.stream()
                      .map(o -> JacksonUtil.jsonToObject(JacksonUtil.objectToJson(o), type))
                      .toList());
            });
  }

  /**
   * Retrieves all pages of data from the specified URI with the provided access token, handling
   * errors and retries as per the provided properties, and applying server-side filtering.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param url The base URL from which data is retrieved.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param type The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param pageQuery The page query specifying pagination parameters.
   * @param <T> The type of the expected response.
   * @return A Flux emitting the response objects.
   */
  private static <T> Flux<T> retrieveAllPagesWithServerFilter(
      WebClient webClient,
      URI url,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> type,
      BaseClientProperties properties,
      Pageable pageQuery) {
    return recursiveRetrieve(
        webClient, url, headersConsumer, errorhandler, type, properties, pageQuery);
  }

  /**
   * Retrieves a single page of data from the specified URI with the provided access token, handling
   * errors and retries as per the provided properties, and applying the given page query.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param url The base URL from which data is retrieved.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param type The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param pageQuery The page query specifying pagination parameters.
   * @param <T> The type of the expected response.
   * @return A Flux emitting the response objects.
   */
  public static <T> Flux<T> retrieveSinglePage(
      WebClient webClient,
      URI url,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> type,
      BaseClientProperties properties,
      Pageable pageQuery) {
    return retrieveSinglePageWithPageResponse(
            webClient, url, headersConsumer, errorhandler, type, properties, pageQuery)
        .flatMapMany(TmfPage::getContent);
  }

  /**
   * Retrieves a single page of data from the specified URI with the provided access token, handling
   * errors and retries as per the provided properties.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param url The base URL from which data is retrieved.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param type The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param <T> The type of the expected response.
   * @return A Mono emitting the response page.
   */
  public static <T> Mono<TmfPage<Flux<T>>> retrieveSinglePageWithPageResponse(
      WebClient webClient,
      URI url,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> type,
      BaseClientProperties properties) {
    Pageable pageable = TmfOffsetRequest.of(0);
    return retrieveSinglePageWithPageResponse(
        webClient, url, headersConsumer, errorhandler, type, properties, pageable);
  }

  /**
   * Retrieves a single page of data from the specified URI with the provided access token, handling
   * errors and retries as per the provided properties, and applying the given offset request.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param url The base URL from which data is retrieved.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param type The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param offsetRequest The offset request specifying pagination parameters.
   * @param <T> The type of the expected response.
   * @return A Mono emitting the response page.
   */
  public static <T> Mono<TmfPage<Flux<T>>> retrieveSinglePageWithPageResponse(
      WebClient webClient,
      URI url,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> type,
      BaseClientProperties properties,
      Pageable offsetRequest) {
    return getAllRequestWithResponseEntity(
            webClient, url, headersConsumer, errorhandler, type, properties, offsetRequest)
        .map(responseEntity -> preparePagedResponse(responseEntity, offsetRequest));
  }

  /**
   * Retrieves the response entity containing a Flux of data from the specified URI with the
   * provided access token, handling errors and retries as per the provided properties, and applying
   * the given page query.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the GET request is made.
   * @param httpHeadersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param t The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param pageQuery The page query specifying pagination parameters.
   * @param <T> The type of the expected response.
   * @return A Mono emitting the response entity. with header, pageable
   */
  public static <T> Mono<ResponseEntity<Flux<T>>> getAllRequestWithResponseEntity(
      WebClient webClient,
      URI uri,
      Consumer<HttpHeaders> httpHeadersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties,
      Pageable pageQuery) {
    var updatedUri = updateUri(uri, pageQuery);
    return getAllRequestWithResponseEntity(
        webClient, updatedUri, httpHeadersConsumer, errorhandler, t, properties);
  }

  /**
   * Retrieves the response entity containing a Flux of data from the specified URI with the
   * provided access token, handling errors and retries as per the provided properties.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the GET request is made.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param t The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param <T> The type of the expected response.
   * @return A Mono emitting the response entity.
   */
  public static <T> Mono<ResponseEntity<Flux<T>>> getAllRequestWithResponseEntity(
      WebClient webClient,
      URI uri,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> t,
      BaseClientProperties properties) {
    Objects.requireNonNull(t, TmfClientCommonsConstants.ERROR_MSG_NULL_RETURN_CLASS_TYPE);
    validateHeadersConsumer(headersConsumer);
    return webClient
        .get()
        .uri(uri)
        .headers(headersConsumer)
        .retrieve()
        .onStatus(HttpStatusCode::isError, errorhandler)
        .toEntityFlux(t)
        .retryWhen(
            WebClientUtil.retry(
                properties.getNumRetries(), Duration.ofMillis(properties.getRetryWaitMillis())));
  }

  /**
   * Executes a DELETE request to the specified URI with the provided access token, handling errors
   * and retries as per the provided properties.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param uri The URI to which the DELETE request is made.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param properties The properties governing the behavior of the request.
   * @return A Mono emitting when the operation is complete.
   * @throws NullPointerException if any of the required parameters is null.
   */
  public static Mono<Void> deleteRequest(
      WebClient webClient,
      URI uri,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      BaseClientProperties properties) {
    validateHeadersConsumer(headersConsumer);
    return webClient
        .delete()
        .uri(uri)
        .headers(headersConsumer)
        .retrieve()
        .onStatus(HttpStatusCode::isError, errorhandler)
        .toBodilessEntity()
        .retryWhen(
            WebClientUtil.retry(
                properties.getNumRetries(), Duration.ofMillis(properties.getRetryWaitMillis())))
        .then();
  }

  public static <T> Mono<T> deleteRequest(
          WebClient webClient,
          URI uri,
          Consumer<HttpHeaders> headersConsumer,
          Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
          Class<T> t,
          BaseClientProperties properties) {
    validateHeadersConsumer(headersConsumer);
    return webClient
            .delete()
            .uri(uri)
            .headers(headersConsumer)
            .retrieve()
            .onStatus(HttpStatusCode::isError, errorhandler)
            .bodyToMono(t)
            .retryWhen(
                    WebClientUtil.retry(
                            properties.getNumRetries(), Duration.ofMillis(properties.getRetryWaitMillis())));
  }

  /**
   * Recursively retrieves all pages of data from the specified URL with the provided access token,
   * handling errors and retries as per the provided properties, and applying the given page query.
   *
   * @param webClient The WebClient instance to use for making the request.
   * @param url The base URL from which data is retrieved.
   * @param headersConsumer The authentication token used for authorization.
   * @param errorhandler Function to handle errors.
   * @param type The class type of the expected response.
   * @param properties The properties governing the behavior of the request.
   * @param pageQuery The page query specifying pagination parameters.
   * @param <T> The type of the expected response.
   * @return A Flux emitting the response objects.
   */
  private static <T> Flux<T> recursiveRetrieve(
      WebClient webClient,
      URI url,
      Consumer<HttpHeaders> headersConsumer,
      Function<ClientResponse, Mono<? extends Throwable>> errorhandler,
      Class<T> type,
      BaseClientProperties properties,
      Pageable pageQuery) {
    return retrieveSinglePageWithPageResponse(
            webClient, url, headersConsumer, errorhandler, type, properties, pageQuery)
        .flatMapMany(
            (TmfPage<Flux<T>> pagedResponse) -> {
              if (pagedResponse.isLast()) {
                return pagedResponse.getContent();
              } else {
                return Flux.concat(
                    pagedResponse.getContent(),
                    recursiveRetrieve(
                        webClient,
                        url,
                        headersConsumer,
                        errorhandler,
                        type,
                        properties,
                        pagedResponse.getNextPageable()));
              }
            });
  }

  /**
   * Prepares a paged response from the provided response entity and page query.
   *
   * @param responseEntity The response entity containing the data.
   * @param tmfPageable The pageable object representing the page parameters.
   * @param <T> The type of the response data.
   * @return A TmfPage containing the paged response.
   */
  private static <T> TmfPage<Flux<T>> preparePagedResponse(
      ResponseEntity<Flux<T>> responseEntity, Pageable tmfPageable) {
    long total = getXTotalCount(responseEntity);
    int serverItemCount = getContentRange(responseEntity);
    return new OffsetPage<>(total, serverItemCount, tmfPageable, responseEntity.getBody());
  }

  private static long getXTotalCount(ResponseEntity<?> responseEntity) {
    var value = TmfClientCommonHeaderUtil.getHeadersFirstValue("X-Total-Count", responseEntity);
    return hasText(value) ? Long.parseLong(value) : 0;
  }

  private static int getContentRange(ResponseEntity<?> responseEntity) {
    var value = TmfClientCommonHeaderUtil.getHeadersFirstValue("Content-Range", responseEntity);
    return hasText(value) ? getItemCountFromContentRange(value) : 0;
  }

  private static int getItemCountFromContentRange(String contentRange) {
    String[] parts = contentRange.replace("items ", "").split("/");
    if (parts.length != 2) {
        return 0;
    }
    // if totalMatchedCount is 0, then return 0. Handle '*' for unknown size as well
    if (!parts[1].contains("*") && ((toInt(parts[1].trim(), 0)) == 0)) {
      return 0;
    }
    // '*' can be used to indicate no results returned
    if (parts[0].contains("*")) {
      return 0;
    }
    String[] rangeParts = parts[0].split("-");
    // this is a safeguard to ensure '-' exists. If not we are assuming no rows returned
    if (rangeParts.length != 2) {
      return 0;
    }
    var rangeStart = Integer.parseInt(rangeParts[0].trim());
    var rangeEnd = Integer.parseInt(rangeParts[1].trim());
    // handle 0-0/N as well. This is nonstandard and indicates no rows returned
    if (rangeStart == 0 && rangeEnd == 0) {
      return 0;
    }
    return rangeEnd - rangeStart + 1;
  }

  public static TmfClientException createException(
      HttpStatusCode httpStatusCode,
      ErrorMessage errorMessage,
      Class<? extends TmfClientException> exception) {
    try {
      return exception
          .getDeclaredConstructor(HttpStatusCode.class, ErrorMessage.class)
          .newInstance(httpStatusCode, errorMessage);
    } catch (Exception e) {
      throw new IllegalArgumentException("Exception class misses required constructor.", e);
    }
  }

  public static TmfClientException createException(
      HttpStatusCode httpStatusCode,
      String message,
      Class<? extends TmfClientException> exception) {
    try {
      return exception
          .getDeclaredConstructor(HttpStatusCode.class, String.class)
          .newInstance(httpStatusCode, message);
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Can't instantiate " + exception.getSimpleName() + " with httpStatusCode and String", e);
    }
  }

  public static TmfClientException createException(ClientResponse r, Class<? extends TmfClientException> e) {
    ErrorMessage errorMessage = new ErrorMessage();
    HttpStatusCode httpStatusCode = r.statusCode();
    errorMessage.setCode(String.valueOf(httpStatusCode.value()));
    var status = findStatusText(httpStatusCode);
    errorMessage.setStatus(status);
    errorMessage.setMessage(
            String.format(
                    "Got %d %s on %s %s",
                    httpStatusCode.value(), status, r.request().getMethod(), r.request().getURI()));
    return createException(httpStatusCode, errorMessage, e);
  }

  public static String findStatusText(HttpStatusCode sc) {
    return Optional.ofNullable(HttpStatus.resolve(sc.value()))
            .map(HttpStatus::getReasonPhrase)
            .orElse("Unknown Status");
  }

  private static void validateHeadersConsumer(Consumer<HttpHeaders> headersConsumer) {
    if (headersConsumer != null) {
      var headers = new HttpHeaders();
      headersConsumer.accept(headers);
      validateAuthorizationHeader(headers);
    } else {
      throw new IllegalArgumentException(TmfClientCommonsConstants.ERROR_MSG_NULL_HEADERS_CONSUMER);
    }
  }

  private static HttpHeaders prepareAndValidatePatchHeaders(
      Consumer<HttpHeaders> headersConsumer, String expectedContentType) {
    var headers =
        prepareAndValidateHeaders(headersConsumer, MediaType.valueOf(expectedContentType));
    MediaType contentType = headers.getContentType();
    if (contentType != null && !contentType.toString().startsWith(expectedContentType)) {
      log.warn("PATCH request has Content-Type '{}' but expected '{}'. "
          + "Consider updating your headers configuration.", contentType, expectedContentType);
    }
    return headers;
  }

  private static HttpHeaders prepareAndValidateHeaders(
      Consumer<HttpHeaders> headersConsumer, MediaType defaultContentType) {
    if (headersConsumer == null) {
      throw new IllegalArgumentException(TmfClientCommonsConstants.ERROR_MSG_NULL_HEADERS_CONSUMER);
    }
    var headers = new HttpHeaders();
    headersConsumer.accept(headers);
    validateAuthorizationHeader(headers);
    if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
      headers.setContentType(defaultContentType);
    }
    return headers;
  }

  private static void validateAuthorizationHeader(HttpHeaders headers) {
    String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
    if (!StringUtils.hasText(authorizationHeader)) {
      throw new IllegalArgumentException(TmfClientCommonsConstants.ERROR_MSG_EMPTY_AUTH_TOKEN);
    }
  }
}
