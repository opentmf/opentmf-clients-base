package com.pia.tmf.common.config;

import com.pia.tmf.common.client.api.GenericClient;
import com.pia.tmf.common.config.TmfClientConfigurations.TmfClientConfig;
import lombok.RequiredArgsConstructor;
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
