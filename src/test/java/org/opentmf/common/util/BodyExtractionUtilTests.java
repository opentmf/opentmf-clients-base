package org.opentmf.common.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentmf.common.model.ErrorMessage;

/**
 * @author Gokhan Demir
 */
class BodyExtractionUtilTests {

  static Stream<ErrorMessage> positiveVariants() {
    return IntStream.range(0, 16) // 0 … 15
        .filter(mask -> mask != 15)
        .mapToObj(
            mask ->
                msg(
                    (mask & 1) != 0 ? null : "A",
                    (mask & 2) != 0 ? null : "B",
                    (mask & 4) != 0 ? null : "C",
                    (mask & 8) != 0 ? null : "D"));
  }

  @ParameterizedTest
  @MethodSource("positiveVariants")
  void testErrorMessage_hasMeaningfulFields_returnsTrue(ErrorMessage em) {
    Assertions.assertTrue(BodyExtractionUtil.hasMeaningfulFields(em));
  }

  @Test
  void testErrorMessage_hasMeaningfulFields_returnsFalse() {
    Assertions.assertFalse(BodyExtractionUtil.hasMeaningfulFields(new ErrorMessage()));
  }

  @Test
  void testSafeString_withNullByteArray_returnsBinaryPayload() {
    Assertions.assertEquals("<binary payload>", BodyExtractionUtil.safeString(null));
  }

  @Test
  void testSafeString_withUtf8String_returnsUtf8String() {
    Assertions.assertEquals(
        "utf8 string", BodyExtractionUtil.safeString("utf8 string".getBytes(UTF_8)));
  }

  @Test
  void testSafeString_withBinaryData_returnsBinaryPayload() {
    Assertions.assertEquals(
        "<binary payload>", BodyExtractionUtil.safeString(new byte[] {'\0', '\1', '\2'}));
  }

  @Test
  void testSafeString_withEmptyByteArray_returnsBinaryPayload() {
    Assertions.assertEquals("<binary payload>", BodyExtractionUtil.safeString(new byte[0]));
  }

  @Test
  void testSafeString_withNonUtf8ByteArray_returnsBinaryPayload() {
    Assertions.assertEquals(
        "<binary payload>", BodyExtractionUtil.safeString(new byte[] {(byte) 0xC3, (byte) 0x28}));
  }

  private static ErrorMessage msg(String code, String reason, String message, String status) {
    ErrorMessage em = new ErrorMessage();
    em.setCode(code);
    em.setReason(reason);
    em.setMessage(message);
    em.setStatus(status);
    return em;
  }
}
