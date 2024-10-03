package com.pia.tmf.common.model;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Gokhan Demir
 */
@Getter
@Setter
public class ErrorMessage implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * Application relevant detail, defined in the API or a common list.
   */
  private String code;

  /**
   * Explanation of the reason for the error which can be shown to a client user.
   */
  private String reason;

  /**
   * More details and corrective actions related to the error which can be shown
   * to a client user.
   */
  private String message;

  /**
   * HTTP Error code extension.
   */
  private String status;

  /**
   * URI of documentation describing the error.
   */
  private URI referenceError;

  @Override
  public String toString() {
    return "HTTP " + code + " " + status + ". Reason: " + reason + ". Message: " + message;
  }
}
