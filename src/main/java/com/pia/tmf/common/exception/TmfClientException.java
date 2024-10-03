package com.pia.tmf.common.exception;

import com.pia.client.common.exception.PiaWebClientException;
import com.pia.tmf.common.model.ErrorMessage;
import java.io.Serial;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

/**
 * @author Gokhan Demir
 */
@Getter
public class TmfClientException extends PiaWebClientException {

  @Serial
  private static final long serialVersionUID = 1L;

  private final ErrorMessage errorMessage;

  public TmfClientException(HttpStatusCode httpStatusCode) {
    super(httpStatusCode);
    this.errorMessage = null;
  }

  public TmfClientException(HttpStatusCode httpStatusCode, String message) {
    super(httpStatusCode, message);
    this.errorMessage = null;
  }

  public TmfClientException(HttpStatusCode httpStatusCode, ErrorMessage errorMessage) {
    super(httpStatusCode);
    this.errorMessage = errorMessage;
  }

  @Override
  public String getMessage() {
    if (errorMessage != null) {
      return errorMessage.toString();
    }
    return super.getMessage();
  }
}
