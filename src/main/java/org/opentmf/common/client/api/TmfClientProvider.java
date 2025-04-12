package org.opentmf.common.client.api;

import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;

/**
 * @author Gokhan Demir
 */
public interface TmfClientProvider<T extends TmfClient<?, ?, ?>> {

  /**
   * Constructs and returns a new client according to the desired configuration and the web client
   * type.
   *
   * @param config        The TMF client configuration.
   * @param clientId  The configured webClient id that will be considered as the preffix ffor three
   *                  exposed beans: WebClient, TokenService and ClientProperties.
   * @return a new TMF client according to the desired configuration and the web client type.
   */
  T getTmfClient(TmfClientConfig config, String clientId);
}
