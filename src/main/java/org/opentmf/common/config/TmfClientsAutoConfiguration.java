package org.opentmf.common.config;

import lombok.extern.slf4j.Slf4j;
import org.opentmf.client.common.service.api.TokenService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @author Gokhan Demir
 */
@AutoConfiguration(after = TokenService.class)
@EnableConfigurationProperties(TmfClientConfigurations.class)
@Slf4j
public class TmfClientsAutoConfiguration {

  @Bean
  public GenericClientProvider genericClientProvider(ApplicationContext ctx) {
    return new GenericClientProvider(ctx);
  }
}
