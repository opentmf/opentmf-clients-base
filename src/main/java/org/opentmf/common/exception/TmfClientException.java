package org.opentmf.common.exception;

import java.io.Serial;
import lombok.Getter;
import org.opentmf.client.common.exception.OpenTmfWebClientException;
import org.opentmf.common.model.ErrorMessage;
import org.springframework.http.HttpStatusCode;

/**
 * @author Gokhan Demir
 */
@Getter
public class TmfClientException extends OpenTmfWebClientException {

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
