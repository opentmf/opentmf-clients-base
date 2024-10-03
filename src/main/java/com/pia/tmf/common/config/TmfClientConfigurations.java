package com.pia.tmf.common.config;

import com.pia.tmf.common.model.Scope;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Gokhan Demir
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "pia")
public class TmfClientConfigurations {

  @NotEmpty
  private Map<String, TmfClientConfig> tmfClients;

  @Valid
  @Getter
  @Setter
  public static class TmfClientConfig {

    /**
     * The optional baseUrl for this client.
     */
    @NotBlank
    private String baseUrl;

    /**
     * The optional context path.
     *
     * <p>Example: <code>/tmf-api/resourceOrderingManagement/v4</code></p>
     */
    private String contextPath;

    @NotBlank
    private String endpoint;

    /**
     * A map of String and client path and scope information.
     */
    private Map<Scope, String> scopes = new EnumMap<>(Scope.class);

    /**
     * Optional map of header name and static header value. When specified, these headers will
     * automatically be set in the configured WebClient.
     */
    private Map<String, String> fixedHeaders;
  }
}
