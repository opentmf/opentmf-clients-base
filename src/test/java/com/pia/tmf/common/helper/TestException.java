package com.pia.tmf.common.helper;

import com.pia.tmf.common.exception.TmfClientException;
import com.pia.tmf.common.model.ErrorMessage;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * @author Gokhan Demir
 */
@Getter
public class TestException extends TmfClientException {

  private final ErrorMessage errorMessage;

  public TestException(HttpStatusCode httpStatusCode) {
    super(httpStatusCode);
    this.errorMessage = null;
  }

  public TestException(HttpStatusCode httpStatusCode, String message) {
    super(httpStatusCode, message);
    this.errorMessage = null;
  }

  public TestException(HttpStatusCode httpStatusCode, ErrorMessage errorMessage) {
    super(httpStatusCode);
    this.errorMessage = errorMessage;
  }
}
