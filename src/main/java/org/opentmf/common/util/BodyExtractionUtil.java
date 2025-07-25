package org.opentmf.common.util;

import lombok.experimental.UtilityClass;
import org.opentmf.common.model.ErrorMessage;
import org.opentmf.commons.util.JacksonUtil;

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

/**
 * @author Gokhan Demir
 */
@UtilityClass
public class BodyExtractionUtil {

  private static final String BINARY_FALLBACK = "<binary payload>";

  public static String safeString(byte[] body) {
    if (body == null || body.length == 0) {
      return BINARY_FALLBACK;
    }

    CharsetDecoder utf8 =
        StandardCharsets.UTF_8
            .newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT);

    try {
      String decoded = utf8.decode(ByteBuffer.wrap(body)).toString();

      // Heuristic: bail out if too many control characters remain
      long controlCharacterCount =
          decoded.chars().filter(c -> c < 0x20 && c != '\n' && c != '\r' && c != '\t').count();
      return (controlCharacterCount > decoded.length() / 10)
        ? BINARY_FALLBACK
        : decoded;
    } catch (CharacterCodingException ex) {
      return BINARY_FALLBACK;
    }
  }

  public static boolean hasMeaningfulFields(ErrorMessage em) {
    return em.getCode() != null
        || em.getReason() != null
        || em.getMessage() != null
        || em.getStatus() != null;
  }

  public static ErrorMessage tryParseJson(byte[] body) {
    try {
      return JacksonUtil.getDefaultObjectMapper().readValue(body, ErrorMessage.class);
    } catch (Exception ignore) {
      return null;
    }
  }
}
