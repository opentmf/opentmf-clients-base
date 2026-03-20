package org.opentmf.common.model;

/**
 * @author Gokhan Demir
 */
public final class TmfClientCommonsConstants {

  public static final String ERROR_MSG_EMPTY_AUTH_TOKEN = "AuthToken can not be empty";
  public static final String ERROR_MSG_NULL_HEADERS_CONSUMER = "Headers consumer can not be null";
  public static final String ERROR_MSG_EMPTY_RESPONSE = "Empty body received from %s";
  public static final String ERROR_MSG_ID_RESPONSE = "ID can not be empty";

  public static final String ERROR_MSG_NULL_RETURN_CLASS_TYPE = "Class type can not be null";
  public static final String MEDIA_TYPE_JSON_PATCH = "application/json-patch+json";
  public static final String MEDIA_TYPE_MERGE_PATCH = "application/merge-patch+json";
  private TmfClientCommonsConstants() {}
}
