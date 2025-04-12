package org.opentmf.common.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gokhan Demir
 * @author Yusuf Bozkurt
 */
public enum Scope {

  GET("get"),
  LIST("list"),
  POST("post"),
  PATCH("patch"),

  DELETE("delete");

  private final String value;

  Scope(String value) {
    this.value = value;
  }

  @JsonDeserialize
  public String getValue() {
    return value;
  }

  private static final Map<String, Scope> REVERSE_MAP = new HashMap<>();

  static {
    for (Scope scope : Scope.values()) {
      REVERSE_MAP.put(scope.getValue(), scope);
    }
  }

  @JsonCreator
  public static Scope fromValue(String value) {
    if (REVERSE_MAP.containsKey(value)) {
      return REVERSE_MAP.get(value);
    }
    throw new IllegalArgumentException("Unsupported scope key '" + value + "'");
  }

}
