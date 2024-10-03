package com.pia.tmf.common.model;

import io.micrometer.common.util.StringUtils;
import lombok.Getter;

/**
 * Represents a JSON-based filter for querying data.
 */
@Getter
public class JsonFilter {

  private final String query;
  private final TYPE type;

  /**
   * Constructs a new JsonFilter object with the provided query string and filter type.
   * @param query The JSON filter query string.
   * @param type The type of JSON filter.
   * @throws IllegalArgumentException if the query string is null or empty.
   */
  private JsonFilter(String query, TYPE type) {
    if (StringUtils.isBlank(query)) {
      throw new IllegalArgumentException("JsonFilter query must not be null or empty!");
    }
    this.query = query;
    this.type = type;
  }

  public static JsonFilter of(String query, TYPE type) {
    return new JsonFilter(query, type);
  }

  public static JsonFilter of(String query) {
    return new JsonFilter(query, TYPE.SERVER);
  }

  /**
   * Enumeration representing the type of JSON filter.
   * The type can be either CLIENT or SERVER.
   * CLIENT: The filter is applied on the client side.
   * SERVER: The filter is applied on the server side.
   */
  public enum TYPE {
    CLIENT,
    SERVER
  }
}
