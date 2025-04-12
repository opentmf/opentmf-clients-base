package org.opentmf.common.config;

import lombok.RequiredArgsConstructor;
import org.opentmf.client.common.model.BaseClientProperties;
import org.opentmf.client.common.service.api.TokenService;
import org.opentmf.common.client.api.GenericClient;
import org.opentmf.common.client.api.TmfClientProvider;
import org.opentmf.common.client.impl.GenericClientImpl;
import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;
import org.opentmf.common.util.TmfClientConstants;
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
