package org.opentmf.common.config;

import lombok.RequiredArgsConstructor;
import org.opentmf.client.openid.config.OpenidWebClientProviderAutoConfiguration;
import org.opentmf.client.openid.model.OpenidClientProperties;
import org.opentmf.client.openid.model.OpenidClients;
import org.opentmf.client.openid.service.api.OpenidTokenService;
import org.opentmf.client.openid.service.api.OpenidWebClientProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Gokhan Demir
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(OpenidClients.class)
@Import(OpenidWebClientProviderAutoConfiguration.class)
public class OpenidAuthClientsConfig {

  private final OpenidWebClientProvider openidWebClientProvider;
  private final OpenidClients openidClients;

  @Bean
  public OpenidClientProperties shClientProperties() {
    return openidClients.getOpenid().get("sh-client");
  }

  @Bean
  public WebClient shWebClient(OpenidClientProperties shClientProperties) {
    return openidWebClientProvider.buildWebClient(shClientProperties);
  }

  @Bean
  public OpenidTokenService shTokenService(OpenidClientProperties shClientProperties) {
    return openidWebClientProvider.buildTokenService(shClientProperties);
  }
}
