package org.opentmf.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Gokhan Demir
 */
class ConverterUtilTests {

  @Test
  void testToInt_withNullValue_returnsDefaultValue() {
    Assertions.assertEquals(123, ConverterUtil.toInt(null, 123));
  }

  @Test
  void testToInt_withNumberText_returnsParsedNumber() {
    Assertions.assertEquals(123, ConverterUtil.toInt("123", 123));
  }
}
