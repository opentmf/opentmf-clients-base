package com.pia.tmf.common.client.impl;

import com.github.fge.jsonpatch.JsonPatch;
import com.pia.client.common.model.BaseClientProperties;
import com.pia.client.common.service.api.TokenService;
import com.pia.tmf.common.client.api.TmfClient;
import com.pia.tmf.common.config.TmfClientConfigurations.TmfClientConfig;
import com.pia.tmf.common.exception.TmfClientException;
import com.pia.tmf.common.model.*;
import com.pia.tmf.common.util.TmfClientCommonHeaderUtil;
import com.pia.tmf.common.util.TmfClientCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
public abstract class TmfClientBaseImpl<C, U, R> implements TmfClient<C, U, R> {

  /**
   * Returns the class type of R generics.
   *
   * @return the class type of R generics.
   */
  protected abstract Class<R> getType();

  protected abstract Class<? extends TmfClientException> getExceptionType();

  protected abstract TmfClientConfig getClientConfig();

  protected abstract WebClient getWebClient();

  protected abstract TokenService getTokenService();

  protected abstract BaseClientProperties getClientProperties();

  /**
   * Handles errors.
   *
   * @param clientResponse The ClientResponse containing information about the error.
   * @return A Mono emitting an error or an empty Mono if the error is handled.
   */
  protected Mono<Throwable> handleError(ClientResponse clientResponse) {
    var httpStatus = clientResponse.statusCode();
    log.debug(
        "Handling httpStatus = {} for {} {}",
        httpStatus,
        clientResponse.request().getMethod(),
        clientResponse.request().getURI());
    return clientResponse
        .bodyToMono(ErrorMessage.class)
        .switchIfEmpty(
            Mono.defer(
                () ->
                    Mono.error(
                        TmfClientCommonUtil.createException(httpStatus, getExceptionType()))))
        .map(
            errorContext ->
                TmfClientCommonUtil.createException(httpStatus, errorContext, getExceptionType()));
  }

  protected final Mono<String> getToken(Scope scope) {
    return TmfClientCommonUtil.getToken(scope, getClientConfig(), getTokenService());
  }

  @Override
  public Mono<R> get(String id) {
    return getToken(Scope.GET).flatMap(token -> getWithToken(token, id));
  }

  @Override
  public Mono<R> get(String id, TmfRequestContext requestContext) {
    return get(id, requestContext, getType());
  }

  @Override
  public <T> Mono<T> get(String id, Class<T> type) {
    return getToken(Scope.GET).flatMap(token -> getWithToken(token, id, type));
  }

  @Override
  public <T> Mono<T> get(String id, TmfRequestContext requestContext, Class<T> type) {
    return getToken(Scope.GET).flatMap(token -> getWithToken(token, id, requestContext, type));
  }

  @Override
  public Mono<R> getWithToken(String token, String id) {
    return getWithToken(token, id, getType());
  }

  @Override
  public Mono<R> getWithToken(String token, String id, TmfRequestContext requestContext) {
    return getWithToken(token, id, requestContext, getType());
  }

  @Override
  public <T> Mono<T> getWithToken(String token, String id, Class<T> type) {
    var uri = TmfClientCommonUtil.buildUriWithId(getClientConfig(), id);
    return TmfClientCommonUtil.getRequest(
        getWebClient(),
        uri,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), getTokenService()),
        this::handleError,
        type,
        getClientProperties());
  }

  @Override
  public <T> Mono<T> getWithToken(
      String token, String id, TmfRequestContext requestContext, Class<T> type) {
    var uri = TmfClientCommonUtil.buildUriWithId(getClientConfig(), id, requestContext);
    return TmfClientCommonUtil.getRequest(
        getWebClient(),
        uri,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), requestContext, getTokenService()),
        this::handleError,
        type,
        getClientProperties(),
        requestContext);
  }

  @Override
  public Flux<R> list() {
    return getToken(Scope.LIST).flatMapMany(this::listWithToken);
  }

  @Override
  public <T> Flux<T> list(Class<T> type) {
    return getToken(Scope.LIST).flatMapMany(token -> listWithToken(token, type));
  }

  @Override
  public Flux<R> list(Pageable pageable) {
    return getToken(Scope.LIST).flatMapMany(token -> listWithToken(token, pageable));
  }

  @Override
  public <T> Flux<T> list(Pageable request, Class<T> type) {
    return getToken(Scope.LIST).flatMapMany(token -> listWithToken(token, request, type));
  }

  @Override
  public Flux<R> listWithToken(String token) {
    return listWithToken(token, getType());
  }

  @Override
  public <T> Flux<T> listWithToken(String token, Class<T> type) {
    return listWithToken(token, TmfOffsetRequest.of(0), type);
  }

  @Override
  public Flux<R> listWithToken(String token, Pageable pageable) {
    return listWithToken(token, pageable, getType());
  }

  @Override
  public <T> Flux<T> listWithToken(String token, Pageable request, Class<T> type) {
    var uri = TmfClientCommonUtil.buildUri(getClientConfig());
    return TmfClientCommonUtil.retrieveSinglePage(
        getWebClient(),
        uri,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), getTokenService()),
        this::handleError,
        type,
        getClientProperties(),
        request);
  }

  @Override
  public Flux<R> listAll() {
    return getToken(Scope.LIST).flatMapMany(this::listAllWithToken);
  }

  @Override
  public <T> Flux<T> listAll(Class<T> type) {
    return getToken(Scope.LIST).flatMapMany(token -> listAllWithToken(token, type));
  }

  @Override
  public Flux<R> listAll(Pageable pageable) {
    return getToken(Scope.LIST).flatMapMany(token -> listAllWithToken(token, pageable));
  }

  @Override
  public <T> Flux<T> listAll(Pageable pageable, Class<T> type) {
    return getToken(Scope.LIST).flatMapMany(token -> listAllWithToken(token, pageable, type));
  }

  @Override
  public Flux<R> listAllWithToken(String token) {
    return listAllWithToken(token, getType());
  }

  @Override
  public <T> Flux<T> listAllWithToken(String token, Class<T> type) {
    return listAllWithToken(token, TmfOffsetRequest.of(), type);
  }

  @Override
  public Flux<R> listAllWithToken(String token, Pageable pageable) {
    return listAllWithToken(token, pageable, getType());
  }

  @Override
  public <T> Flux<T> listAllWithToken(String token, Pageable pageable, Class<T> type) {
    var uri = TmfClientCommonUtil.buildUri(getClientConfig());
    return TmfClientCommonUtil.retrieveAllPages(
        getWebClient(),
        uri,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), pageable, getTokenService()),
        this::handleError,
        type,
        getClientProperties(),
        pageable);
  }

  @Override
  public Mono<TmfPage<Flux<R>>> listPaged() {
    return getToken(Scope.LIST).flatMap(this::listPagedWithToken);
  }

  @Override
  public <T> Mono<TmfPage<Flux<T>>> listPaged(Class<T> type) {
    return getToken(Scope.LIST).flatMap(token -> listPagedWithToken(token, type));
  }

  @Override
  public Mono<TmfPage<Flux<R>>> listPaged(Pageable pageable) {
    return getToken(Scope.LIST).flatMap(token -> listPagedWithToken(token, pageable));
  }

  @Override
  public <T> Mono<TmfPage<Flux<T>>> listPaged(Pageable pageable, Class<T> type) {
    return getToken(Scope.LIST).flatMap(token -> listPagedWithToken(token, pageable, type));
  }

  @Override
  public Mono<TmfPage<Flux<R>>> listPagedWithToken(String token) {
    return listPagedWithToken(token, getType());
  }

  @Override
  public <T> Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Class<T> type) {
    return listPagedWithToken(token, TmfOffsetRequest.of(), type);
  }

  @Override
  public Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, Pageable pageable) {
    return listPagedWithToken(token, pageable, getType());
  }

  @Override
  public <T> Mono<TmfPage<Flux<T>>> listPagedWithToken(
      String token, Pageable pageable, Class<T> type) {
    var uri = TmfClientCommonUtil.buildUri(getClientConfig());
    return TmfClientCommonUtil.retrieveSinglePageWithPageResponse(
        getWebClient(),
        uri,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), getTokenService()),
        this::handleError,
        type,
        getClientProperties(),
        pageable);
  }

  @Override
  public Mono<R> post(C obj) {
    return getToken(Scope.POST).flatMap(token -> postWithToken(token, obj));
  }

  @Override
  public Mono<R> post(C obj, TmfRequestContext requestContext) {
    return getToken(Scope.POST).flatMap(token -> postWithToken(token, obj, requestContext));
  }

  @Override
  public <T> Mono<T> post(C obj, Class<T> type) {
    return getToken(Scope.POST).flatMap(token -> postWithToken(token, obj, type));
  }

  @Override
  public <T> Mono<T> post(C obj, TmfRequestContext requestContext, Class<T> type) {
    return getToken(Scope.POST).flatMap(token -> postWithToken(token, obj, requestContext, type));
  }

  @Override
  public Mono<R> postWithToken(String token, C obj) {
    return postWithToken(token, obj, getType());
  }

  @Override
  public Mono<R> postWithToken(String token, C obj, TmfRequestContext requestContext) {
    return postWithToken(token, obj, requestContext, getType());
  }

  @Override
  public <T> Mono<T> postWithToken(String token, C obj, Class<T> type) {
    return postWithToken(token, obj, null, type);
  }

  @Override
  public <T> Mono<T> postWithToken(
      String token, C obj, TmfRequestContext requestContext, Class<T> type) {
    var uri = TmfClientCommonUtil.buildUri(getClientConfig(), requestContext);
    return TmfClientCommonUtil.postRequest(
        getWebClient(),
        uri,
        obj,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), requestContext, getTokenService()),
        type,
        this::handleError,
        getClientProperties());
  }

  @Override
  public Mono<R> patch(String id, U obj) {
    return patch(id, obj, getType());
  }

  @Override
  public Mono<R> patch(String id, U obj, TmfRequestContext requestContext) {
    return patch(id, obj, requestContext, getType());
  }

  @Override
  public <T> Mono<T> patch(String id, U obj, Class<T> type) {
    return patch(id, obj, null, type);
  }

  @Override
  public <T> Mono<T> patch(String id, U obj, TmfRequestContext requestContext, Class<T> type) {
    return getToken(Scope.PATCH)
        .flatMap(token -> patchWithToken(token, id, obj, requestContext, type));
  }

  @Override
  public Mono<R> patch(String id, JsonPatch jsonPatch) {
    return patch(id, jsonPatch, getType());
  }

  public Mono<R> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext) {
    return patch(id, jsonPatch, requestContext, getType());
  }

  @Override
  public <T> Mono<T> patch(String id, JsonPatch jsonPatch, Class<T> type) {
    return patch(id, jsonPatch, null, type);
  }

  @Override
  public <T> Mono<T> patch(
      String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type) {
    return getToken(Scope.PATCH)
        .flatMap(token -> patchWithToken(token, id, jsonPatch, requestContext, type));
  }

  @Override
  public Mono<R> patchWithToken(String token, String id, U obj) {
    return patchWithToken(token, id, obj, getType());
  }

  @Override
  public Mono<R> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext) {
    return patchWithToken(token, id, obj, requestContext, getType());
  }

  @Override
  public <T> Mono<T> patchWithToken(String token, String id, U obj, Class<T> type) {
    return patchWithToken(token, id, obj, null, type);
  }

  public <T> Mono<T> patchWithToken(
      String token, String id, U obj, TmfRequestContext requestContext, Class<T> type) {
    var uri = TmfClientCommonUtil.buildUriWithId(getClientConfig(), id, requestContext);
    return TmfClientCommonUtil.patchRequest(
        getWebClient(),
        uri,
        obj,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), requestContext, getTokenService()),
        this::handleError,
        type,
        getClientProperties());
  }

  @Override
  public Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch) {
    return patchWithToken(token, id, jsonPatch, getType());
  }

  @Override
  public Mono<R> patchWithToken(
      String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext) {
    return patchWithToken(token, id, jsonPatch, requestContext, getType());
  }

  @Override
  public <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, Class<T> type) {
    return patchWithToken(token, id, jsonPatch, null, type);
  }

  @Override
  public <T> Mono<T> patchWithToken(
      String token,
      String id,
      JsonPatch jsonPatch,
      TmfRequestContext requestContext,
      Class<T> type) {
    var uri = TmfClientCommonUtil.buildUriWithId(getClientConfig(), id, requestContext);
    return TmfClientCommonUtil.patchRequest(
        getWebClient(),
        uri,
        jsonPatch,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), requestContext, getTokenService()),
        this::handleError,
        type,
        getClientProperties());
  }

  @Override
  public Mono<Void> delete(String id) {
    return getToken(Scope.DELETE).flatMap(token -> deleteWithToken(token, id));
  }

  @Override
  public Mono<Void> deleteWithToken(String token, String id) {
    var uri = TmfClientCommonUtil.buildUriWithId(getClientConfig(), id);
    return TmfClientCommonUtil.deleteRequest(
        getWebClient(),
        uri,
        TmfClientCommonHeaderUtil.prepareHeaderConsumer(
            token, getClientConfig(), getTokenService()),
        this::handleError,
        getClientProperties());
  }
}
