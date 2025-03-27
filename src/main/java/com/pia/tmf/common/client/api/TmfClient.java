package com.pia.tmf.common.client.api;

import com.github.fge.jsonpatch.JsonPatch;
import com.pia.tmf.common.model.TmfPage;
import com.pia.tmf.common.model.TmfRequestContext;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
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
  Flux<R> list(Pageable request);
  Flux<R> list(MultiValueMap<String, String> param);
  Flux<R> list(MultiValueMap<String, String> param, Pageable request);

  Flux<R> listWithToken(String token);
  Flux<R> listWithToken(String token, Pageable request);
  Flux<R> listWithToken(String token, MultiValueMap<String, String> param);
  Flux<R> listWithToken(String token, MultiValueMap<String, String> param, Pageable request);

  Flux<R> listAll();
  Flux<R> listAll(Pageable request);
  Flux<R> listAll(MultiValueMap<String, String> param);
  Flux<R> listAll(MultiValueMap<String, String> param, Pageable request);

  Flux<R> listAllWithToken(String token);
  Flux<R> listAllWithToken(String token, Pageable request);
  Flux<R> listAllWithToken(String token, MultiValueMap<String, String> param);
  Flux<R> listAllWithToken(String token, MultiValueMap<String, String> param, Pageable request);

  Mono<TmfPage<Flux<R>>> listPaged();
  Mono<TmfPage<Flux<R>>> listPaged(Pageable request);
  Mono<TmfPage<Flux<R>>> listPaged(MultiValueMap<String, String> param);
  Mono<TmfPage<Flux<R>>> listPaged(MultiValueMap<String, String> param, Pageable request);

  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, Pageable request);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, MultiValueMap<String, String> param);
  Mono<TmfPage<Flux<R>>> listPagedWithToken(String token, MultiValueMap<String, String> param, Pageable request);

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
