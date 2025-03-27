package com.pia.tmf.common.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * A utility class representing a filter configuration for querying data. This class allows
 * specifying fields to include or exclude in the query result, as well as applying JSON-based
 * filtering.
 */
@Getter
@Setter
@RequiredArgsConstructor
public class TmfRequestContext {
  /** The set of fields to include or exclude in the query result. */
  private Set<String> fields;

  /** The JSON-based filter to apply to the query. */
  private JsonFilter jsonFilter;

  private MultiValueMap<String, String> headerParameters;

  private MultiValueMap<String, String> queryParameters;

  private TmfRequestContext(Builder builder) {
    this.fields = builder.fields;
    this.jsonFilter = builder.jsonFilter;
    this.headerParameters = builder.headerParameters;
    this.queryParameters = builder.queryParameters;
  }

  /**
   * Retrieves the query string of the JSON filter.
   *
   * @return The query string of the JSON filter, or null if no filter is set.
   */
  public String getJsonFilterQuery() {
    return jsonFilter != null ? jsonFilter.getQuery() : null;
  }

  /**
   * Retrieves the type of the JSON filter.
   *
   * @return The type of the JSON filter, or null if no filter is set.
   */
  public JsonFilter.TYPE getJsonFilterType() {
    return jsonFilter != null ? jsonFilter.getType() : null;
  }

  public static class Builder {
    private Set<String> fields;
    private JsonFilter jsonFilter;
    private MultiValueMap<String, String> headerParameters;
    private MultiValueMap<String, String> queryParameters;

    public Builder withFields(Set<String> fields) {
      this.fields = fields;
      return this;
    }

    public Builder withFields(String... fields) {
      this.fields = (Set.of(fields));
      return this;
    }

    public Builder withServerJsonFilter(String query) {
      this.jsonFilter = JsonFilter.of(query, JsonFilter.TYPE.SERVER);
      return this;
    }

    public Builder withClientJsonFilter(String query) {
      this.jsonFilter = JsonFilter.of(query, JsonFilter.TYPE.CLIENT);
      return this;
    }

    public Builder withJsonFilter(String query) {
      return withServerJsonFilter(query);
    }

    private Builder withJsonFilter(JsonFilter filter) {
      this.jsonFilter = filter;
      return this;
    }

    public Builder withHeaderValues(MultiValueMap<String, String> headerParameters) {
      this.headerParameters = headerParameters;
      return this;
    }

    public Builder withHeaderValues(Map<String, List<String>> map) {
      this.headerParameters = new LinkedMultiValueMap<>(map);
      return this;
    }

    public Builder withHeaderValues(String key, String... values) {
      if (this.headerParameters == null) {
        this.headerParameters = new LinkedMultiValueMap<>();
      }
      this.headerParameters.addAll(key, List.of(values));
      return this;
    }

    public Builder withQueryParameters(MultiValueMap<String, String> queryParameters) {
      this.queryParameters = queryParameters;
      return this;
    }

    public Builder withQueryParameters(String key, String... values) {
      if (this.queryParameters == null) {
        this.queryParameters = new LinkedMultiValueMap<>();
      }
      this.queryParameters.addAll(key, List.of(values));
      return this;
    }

    public TmfRequestContext build() {
      return new TmfRequestContext(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Builder builder(TmfRequestContext requestContext) {
    return builder()
        .withFields(requestContext.getFields())
        .withJsonFilter(requestContext.getJsonFilter())
        .withHeaderValues(requestContext.getHeaderParameters())
        .withQueryParameters(requestContext.getQueryParameters());
  }
}
