package com.pia.tmf.common.client.api;

import com.github.fge.jsonpatch.JsonPatch;
import com.pia.tmf.common.model.TmfPage;
import com.pia.tmf.common.model.TmfRequestContext;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gokhan Demir
 */
public interface TmfClient<C, U, R> {

  Mono<R> get(String id);
  Mono<R> get(String id, TmfRequestContext requestContext);
  <T> Mono<T> get(String id, Class<T> type);
  <T> Mono<T> get(String id, TmfRequestContext requestContext, Class<T> type);

  Mono<R> getWithToken(String token, String id);
  Mono<R> getWithToken(String token, String id, TmfRequestContext requestContext);
  <T> Mono<T> getWithToken(String token, String id, Class<T> type);
  <T> Mono<T> getWithToken(String token, String id, TmfRequestContext requestContext, Class<T> type);

  /**
   * Retrieve the first page with default ordering at server side implementation.
   * @return A flux containing the result objects.
   */
  Flux<R> list();
  <T>Flux<T> list(Class<T> type);
  Flux<R> list(Pageable request);
  <T>Flux<T> list(Pageable request, Class<T> type);

  Flux<R> listWithToken(String token);
  <T>Flux<T> listWithToken(String token, Class<T> type);
  Flux<R> listWithToken(String token, Pageable request);
  <T>Flux<T> listWithToken(String token, Pageable request, Class<T> type);


  Flux<R> listAll();
  <T>Flux<T> listAll(Class<T> type);
  Flux<R> listAll(Pageable request);
  <T>Flux<T> listAll(Pageable request, Class<T> type);


  Flux<R> listAllWithToken(String token);
  <T>Flux<T> listAllWithToken(String token, Class<T> type);
  Flux<R> listAllWithToken(String token, Pageable request);
  <T>Flux<T> listAllWithToken(String token, Pageable request, Class<T> type);


  Mono<TmfPage<Flux<R>>> listPaged();
  <T>Mono<TmfPage<Flux<T>>> listPaged(Class<T> type);
  Mono<TmfPage<Flux<R>>> listPaged(Pageable request);
  <T>Mono<TmfPage<Flux<T>>> listPaged(Pageable request, Class<T> type);


  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token);
  <T>Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Class<T> type);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, Pageable request);
  <T>Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Pageable request, Class<T> type);

  Mono<R> post(C obj);
  Mono<R> post(C obj, TmfRequestContext requestContext);
  <T> Mono<T> post(C obj, Class<T> type);
  <T> Mono<T> post(C obj, TmfRequestContext requestContext, Class<T> type);

  Mono<R> postWithToken(String token, C obj);
  Mono<R> postWithToken(String token, C obj, TmfRequestContext requestContext);
  <T> Mono<T> postWithToken(String token, C obj, Class<T> type);
  <T> Mono<T> postWithToken(String token, C obj, TmfRequestContext requestContext, Class<T> type);

  Mono<R> patch(String id, U obj);
  Mono<R> patch(String id, U obj, TmfRequestContext requestContext);
  <T> Mono<T> patch(String id, U obj, Class<T> type);
  <T> Mono<T> patch(String id, U obj, TmfRequestContext requestContext, Class<T> type);

  Mono<R> patchWithToken(String token, String id, U obj);
  Mono<R> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext);
  <T> Mono<T> patchWithToken(String token, String id, U obj, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext, Class<T> type);

  Mono<R> patch(String id, JsonPatch jsonPatch);
  Mono<R> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type);

  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch);
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type);

  Mono<Void> delete(String id);
  Mono<Void> deleteWithToken(String token, String id);
}
