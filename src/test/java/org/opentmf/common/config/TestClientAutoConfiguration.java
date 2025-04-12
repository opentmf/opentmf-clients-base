package org.opentmf.common.config;

import org.opentmf.client.common.service.api.TokenService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(after = TokenService.class)
public class TestClientAutoConfiguration {

  @Bean
  public TestClientProvider testClientProvider(ApplicationContext ctx) {
    return new TestClientProvider(ctx);
  }
}
