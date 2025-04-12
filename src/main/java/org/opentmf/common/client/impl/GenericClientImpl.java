package org.opentmf.common.client.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.opentmf.client.common.model.BaseClientProperties;
import org.opentmf.client.common.service.api.TokenService;
import org.opentmf.common.client.api.GenericClient;
import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;
import org.opentmf.common.exception.TmfClientException;
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
