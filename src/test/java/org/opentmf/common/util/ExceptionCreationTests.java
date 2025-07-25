package org.opentmf.common.util;

import org.junit.jupiter.api.Test;
import org.opentmf.common.exception.TmfClientException;
import org.opentmf.common.model.ErrorMessage;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opentmf.common.util.TmfClientCommonUtil.createException;

/**
 * To cover edge cases on exception creation.
 *
 * @author Gokhan Demir
 */
class ExceptionCreationTests {

  /** Missing (HttpStatusCode, ErrorMessage) & (HttpStatusCode, String) ctors. */
  static class MissingCtorsEx extends TmfClientException {
    public MissingCtorsEx(HttpStatusCode status) {
      super(status);
    }
  }

  /** Has the right signature but constructor throws. */
  static class ExplodingEx extends TmfClientException {
    public ExplodingEx(HttpStatusCode s, ErrorMessage m) {
      super(s, m);
      throw new RuntimeException("boom");
    }

    public ExplodingEx(HttpStatusCode s, String msg) {
      super(s, msg);
      throw new RuntimeException("boom");
    }
  }

  /** Right signature but private -> IllegalAccessException. */
  static class PrivateCtorEx extends TmfClientException {
    private PrivateCtorEx(HttpStatusCode s, ErrorMessage m) {
      super(s, m);
    }

    private PrivateCtorEx(HttpStatusCode s, String msg) {
      super(s, msg);
    }
  }

  // --- Tests ----------------------------------------------------------------

  @Test
  void missingConstructor_hitsCatch_block_errorMessageVariant1() {
    assertThrows(
        IllegalArgumentException.class,
        () -> createException(HttpStatus.BAD_REQUEST, new ErrorMessage(), MissingCtorsEx.class));
  }

  @Test
  void missingConstructor_hitsCatch_block_errorMessageVariant2() {
    assertThrows(
        IllegalArgumentException.class,
        () -> createException(HttpStatus.BAD_REQUEST, "msg", MissingCtorsEx.class));
  }

  @Test
  void privateConstructor_triggersIllegalAccessException_path() {
    assertThrows(
        IllegalArgumentException.class,
        () -> createException(HttpStatus.BAD_REQUEST, new ErrorMessage(), PrivateCtorEx.class));
    assertThrows(
        IllegalArgumentException.class,
        () -> createException(HttpStatus.BAD_REQUEST, "msg", PrivateCtorEx.class));
  }

  @Test
  void explodingConstructor_triggersInvocationTargetException_path() {
    assertThrows(
        IllegalArgumentException.class,
        () -> createException(HttpStatus.BAD_REQUEST, new ErrorMessage(), ExplodingEx.class));
    assertThrows(
        IllegalArgumentException.class,
        () -> createException(HttpStatus.BAD_REQUEST, "msg", ExplodingEx.class));
  }

  @Test
  void clientResponse_overload_propagates_to_badFactory_and_hitsCatch() {
    // mock ClientResponse + request
    ClientResponse resp = mock(ClientResponse.class);
    when(resp.statusCode()).thenReturn(HttpStatus.BAD_REQUEST);
    HttpRequest req = mock(HttpRequest.class);
    when(req.getMethod()).thenReturn(HttpMethod.GET);
    when(req.getURI()).thenReturn(URI.create("http://example/api"));
    when(resp.request()).thenReturn(req);

    assertThrows(
        IllegalArgumentException.class,
        () -> createException(resp, MissingCtorsEx.class));
  }
}
