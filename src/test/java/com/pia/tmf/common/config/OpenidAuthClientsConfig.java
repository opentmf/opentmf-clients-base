package com.pia.tmf.common.config;

import com.pia.client.openid.config.OpenidWebClientProviderAutoConfiguration;
import com.pia.client.openid.model.OpenidClientProperties;
import com.pia.client.openid.model.OpenidClients;
import com.pia.client.openid.service.api.OpenidTokenService;
import com.pia.client.openid.service.api.OpenidWebClientProvider;
import lombok.RequiredArgsConstructor;
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
