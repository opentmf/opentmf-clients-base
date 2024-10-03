package com.pia.tmf.common.client.impl;

import com.pia.client.common.model.BaseClientProperties;
import com.pia.client.common.service.api.TokenService;
import com.pia.tmf.common.client.api.GenericClient;
import com.pia.tmf.common.config.TmfClientConfigurations.TmfClientConfig;
import com.pia.tmf.common.exception.TmfClientException;
import com.pia.tmf.common.model.TmfPage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
@Getter
public class GenericClientImpl extends
    TmfClientBaseImpl<String, String, String>
    implements GenericClient {

  private final TmfClientConfig clientConfig;
  private final WebClient webClient;
  private final TokenService tokenService;
  private final BaseClientProperties clientProperties;

  @Override
  protected Class<String> getType() {
    return String.class;
  }

  @Override
  protected Class<? extends TmfClientException> getExceptionType() {
    return TmfClientException.class;
  }

  @Override
  public Flux<String> list() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> list(Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> list(MultiValueMap<String, String> param) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> list(MultiValueMap<String, String> param, Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listWithToken(String token) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listWithToken(String token, Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listWithToken(String token, MultiValueMap<String, String> param) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listWithToken(String token, MultiValueMap<String, String> param,
      Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listAll() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listAll(Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listAll(MultiValueMap<String, String> param) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listAll(MultiValueMap<String, String> param, Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listAllWithToken(String token) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listAllWithToken(String token, Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listAllWithToken(String token, MultiValueMap<String, String> param) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Flux<String> listAllWithToken(String token, MultiValueMap<String, String> param,
      Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<TmfPage<Flux<String>>> listPaged() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<TmfPage<Flux<String>>> listPaged(Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<TmfPage<Flux<String>>> listPaged(MultiValueMap<String, String> param) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<TmfPage<Flux<String>>> listPaged(MultiValueMap<String, String> param,
      Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<TmfPage<Flux<String>>> listPagedWithToken(String token) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<TmfPage<Flux<String>>> listPagedWithToken(String token, Pageable pageable) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<TmfPage<Flux<String>>> listPagedWithToken(String token,
      MultiValueMap<String, String> param) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<TmfPage<Flux<String>>> listPagedWithToken(String token,
      MultiValueMap<String, String> param, Pageable pageable) {
    throw new UnsupportedOperationException();
  }
}
