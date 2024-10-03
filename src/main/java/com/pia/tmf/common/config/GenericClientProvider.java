package com.pia.tmf.common.config;

import com.pia.client.common.model.BaseClientProperties;
import com.pia.client.common.service.api.TokenService;
import com.pia.tmf.common.client.api.GenericClient;
import com.pia.tmf.common.client.api.TmfClientProvider;
import com.pia.tmf.common.client.impl.GenericClientImpl;
import com.pia.tmf.common.config.TmfClientConfigurations.TmfClientConfig;
import com.pia.tmf.common.util.TmfClientConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
public class GenericClientProvider implements TmfClientProvider<GenericClient> {

  private final ApplicationContext ctx;

  @Override
  public GenericClient getTmfClient(TmfClientConfig config, String clientId) {
    return new GenericClientImpl(config,
        ctx.getBean(clientId + TmfClientConstants.WEB_CLIENT, WebClient.class),
        ctx.getBean(clientId + TmfClientConstants.TOKEN_SERVICE, TokenService.class),
        ctx.getBean(clientId + TmfClientConstants.CLIENT_PROPERTIES, BaseClientProperties.class));
  }
}
