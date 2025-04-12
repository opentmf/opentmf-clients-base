package org.opentmf.common.config;

import static org.opentmf.common.util.TmfClientConstants.CLIENT_PROPERTIES;
import static org.opentmf.common.util.TmfClientConstants.TOKEN_SERVICE;
import static org.opentmf.common.util.TmfClientConstants.WEB_CLIENT;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.opentmf.client.common.model.BaseClientProperties;
import org.opentmf.client.common.service.api.TokenService;
import org.opentmf.common.client.api.TmfClientProvider;
import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;
import org.opentmf.common.helper.TestResponseModel;
import org.springframework.context.ApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Gokhan Demir
 */
@RequiredArgsConstructor
public class TestClientProvider implements TmfClientProvider<
    TestTmfClient<ObjectNode, TestResponseModel, TestResponseModel>> {

  private final ApplicationContext ctx;

  @Override
  public TestTmfClient<ObjectNode, TestResponseModel, TestResponseModel>
  getTmfClient(TmfClientConfig config, String clientId) {
    return new TestTmfClientImpl<>(TestResponseModel.class,
        config,
        ctx.getBean(clientId + WEB_CLIENT, WebClient.class),
        ctx.getBean(clientId + TOKEN_SERVICE, TokenService.class),
        ctx.getBean(clientId + CLIENT_PROPERTIES, BaseClientProperties.class));
  }
}
