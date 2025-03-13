package com.pia.tmf.common.client.impl;

import com.pia.client.common.model.BaseClientProperties;
import com.pia.client.common.service.api.TokenService;
import com.pia.tmf.common.client.api.GenericClient;
import com.pia.tmf.common.config.TmfClientConfigurations.TmfClientConfig;
import com.pia.tmf.common.exception.TmfClientException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
@Getter
public class GenericClientImpl extends TmfClientBaseImpl<Object, Object, Object>
    implements GenericClient {

  private final TmfClientConfig clientConfig;
  private final WebClient webClient;
  private final TokenService tokenService;
  private final BaseClientProperties clientProperties;

  @Override
  protected Class<Object> getType() {
    return Object.class;
  }

  @Override
  protected Class<? extends TmfClientException> getExceptionType() {
    return TmfClientException.class;
  }
}
