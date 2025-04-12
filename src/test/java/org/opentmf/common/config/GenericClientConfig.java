package org.opentmf.common.config;

import lombok.RequiredArgsConstructor;
import org.opentmf.common.client.api.GenericClient;
import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gokhan Demir
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(TmfClientConfigurations.class)
public class GenericClientConfig {

  private final GenericClientProvider genericClientProvider;
  private final TmfClientConfigurations tmfClientConfigurations;

  @Bean
  public TmfClientConfig testTmfClientConfig() {
    return tmfClientConfigurations.getTmfClients().get("sh-tmf-x-client");
  }

  @Bean
  public GenericClient genericTestClient(
      @Qualifier("testTmfClientConfig") TmfClientConfig testTmfClientConfig) {
    return genericClientProvider.getTmfClient(testTmfClientConfig, "sh");
  }
}
