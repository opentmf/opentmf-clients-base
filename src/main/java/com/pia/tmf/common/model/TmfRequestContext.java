package com.pia.tmf.common.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
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

    private static final String CAN_BE_SET_ONLY_ONCE = "JsonFilter can be set only once.";

    private Set<String> fields;
    private JsonFilter jsonFilter;
    private MultiValueMap<String, String> headerParameters;
    private MultiValueMap<String, String> queryParameters;

    public Builder withFields(@NonNull Set<String> fields) {
      if (this.fields == null) {
        this.fields = new LinkedHashSet<>(fields.size());
      }
      this.fields.addAll(fields);
      return this;
    }

    public Builder withFields(@NonNull String... fields) {
      if (this.fields == null) {
        this.fields = new LinkedHashSet<>(fields.length);
      }
      this.fields.addAll(Set.of(fields));
      return this;
    }

    public Builder withServerJsonFilter(String query) {
      Assert.isNull(this.jsonFilter, CAN_BE_SET_ONLY_ONCE);
      this.jsonFilter = JsonFilter.of(query, JsonFilter.TYPE.SERVER);
      return this;
    }

    public Builder withClientJsonFilter(String query) {
      Assert.isNull(this.jsonFilter, CAN_BE_SET_ONLY_ONCE);
      this.jsonFilter = JsonFilter.of(query, JsonFilter.TYPE.CLIENT);
      return this;
    }

    private Builder withJsonFilter(JsonFilter filter) {
      Assert.isNull(this.jsonFilter, CAN_BE_SET_ONLY_ONCE);
      this.jsonFilter = filter;
      return this;
    }

    public Builder withHeaderValues(@NonNull MultiValueMap<String, String> headerParameters) {
      if (this.headerParameters == null) {
        this.headerParameters = headerParameters;
      } else {
        this.headerParameters.addAll(headerParameters);
      }
      return this;
    }

    public Builder withHeaderValues(String key, String... values) {
      if (this.headerParameters == null) {
        this.headerParameters = new LinkedMultiValueMap<>();
      }
      this.headerParameters.addAll(key, List.of(values));
      return this;
    }

    public Builder withQueryParameters(@NonNull MultiValueMap<String, String> queryParameters) {
      if (this.queryParameters == null) {
        this.queryParameters = queryParameters;
      } else {
        this.queryParameters.addAll(queryParameters);
      }
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
