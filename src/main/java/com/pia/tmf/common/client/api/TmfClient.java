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

  /** these get methods will first retrieve an accessToken */
  Mono<R> get(String id);
  Mono<R> get(String id, TmfRequestContext requestContext);
  <T> Mono<T> get(String id, Class<T> type);
  <T> Mono<T> get(String id, TmfRequestContext requestContext, Class<T> type);

  /** these get methods uses the client provided accessToken */
  Mono<R> getWithToken(String token, String id);
  Mono<R> getWithToken(String token, String id, TmfRequestContext requestContext);
  <T> Mono<T> getWithToken(String token, String id, Class<T> type);
  <T> Mono<T> getWithToken(String token, String id, TmfRequestContext requestContext, Class<T> type);

  /** the list methods serves one page at a time. This group will first retrieve an accessToken */
  Flux<R> list();
  <T>Flux<T> list(Class<T> type);
  Flux<R> list(Pageable request);
  <T>Flux<T> list(Pageable request, Class<T> type);

  /** the list methods serves one page at a time. This group will use the provided accessToken */
  Flux<R> listWithToken(String token);
  <T>Flux<T> listWithToken(String token, Class<T> type);
  Flux<R> listWithToken(String token, Pageable request);
  <T>Flux<T> listWithToken(String token, Pageable request, Class<T> type);

  /** listAll methods retrieves all pages. This group will first retrieve an accessToken */
  Flux<R> listAll();
  <T>Flux<T> listAll(Class<T> type);
  Flux<R> listAll(Pageable request);
  <T>Flux<T> listAll(Pageable request, Class<T> type);

  /** listAll methods retrieves all pages. This group will use the provided accessToken */
  Flux<R> listAllWithToken(String token);
  <T>Flux<T> listAllWithToken(String token, Class<T> type);
  Flux<R> listAllWithToken(String token, Pageable request);
  <T>Flux<T> listAllWithToken(String token, Pageable request, Class<T> type);

  /** these methods retrieve a page. This group will first retrieve an accessToken */
  Mono<TmfPage<Flux<R>>> listPaged();
  <T>Mono<TmfPage<Flux<T>>> listPaged(Class<T> type);
  Mono<TmfPage<Flux<R>>> listPaged(Pageable request);
  <T>Mono<TmfPage<Flux<T>>> listPaged(Pageable request, Class<T> type);

  /** these methods retrieve a page. This group will use the provided accessToken */
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token);
  <T>Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Class<T> type);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, Pageable request);
  <T>Mono<TmfPage<Flux<T>>> listPagedWithToken(String token, Pageable request, Class<T> type);

  /** post a single object to create. This group will first retrieve an accessToken */
  Mono<R> post(C obj);
  Mono<R> post(C obj, TmfRequestContext requestContext);
  <T> Mono<T> post(C obj, Class<T> type);
  <T> Mono<T> post(C obj, TmfRequestContext requestContext, Class<T> type);

  /** post a single object to create. This group will use the provided accessToken */
  Mono<R> postWithToken(String token, C obj);
  Mono<R> postWithToken(String token, C obj, TmfRequestContext requestContext);
  <T> Mono<T> postWithToken(String token, C obj, Class<T> type);
  <T> Mono<T> postWithToken(String token, C obj, TmfRequestContext requestContext, Class<T> type);

  /** merge patch a single object by its id. This group will first retrieve an accessToken */
  Mono<R> patch(String id, U obj);
  Mono<R> patch(String id, U obj, TmfRequestContext requestContext);
  <T> Mono<T> patch(String id, U obj, Class<T> type);
  <T> Mono<T> patch(String id, U obj, TmfRequestContext requestContext, Class<T> type);

  /** merge patch a single object by its id. This group will use the provided accessToken */
  Mono<R> patchWithToken(String token, String id, U obj);
  Mono<R> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext);
  <T> Mono<T> patchWithToken(String token, String id, U obj, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, U obj, TmfRequestContext requestContext, Class<T> type);
  
  /** json patch a single object by its id. This group will first retrieve an accessToken */
  Mono<R> patch(String id, JsonPatch jsonPatch);
  Mono<R> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patch(String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type);

  /** json patch a single object by its id. This group will use the provided accessToken */
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch);
  Mono<R> patchWithToken(String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, Class<T> type);
  <T> Mono<T> patchWithToken(String token, String id, JsonPatch jsonPatch, TmfRequestContext requestContext, Class<T> type);

  /** delete a single object by its id. This group will first retrieve an accessToken */
  Mono<Void> delete(String id);
  Mono<Void> delete(String id, TmfRequestContext requestContext);
  <T> Mono<T> delete(String id, Class<T> type);
  <T> Mono<T> delete(String id, Class<T> type, TmfRequestContext requestContext);

  /** delete a single object by its id. This group will use the provided accessToken */
  Mono<Void> deleteWithToken(String token, String id);
  Mono<Void> deleteWithToken(String token, String id, TmfRequestContext requestContext);
  <T> Mono<T> deleteWithToken(String token, String id, Class<T> type);
  <T> Mono<T> deleteWithToken(String token, String id, Class<T> type, TmfRequestContext requestContext);
}
