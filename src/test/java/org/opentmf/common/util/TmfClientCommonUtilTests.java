package org.opentmf.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Gokhan Demir
 */
class TmfClientCommonUtilTests {

  private static int getItemCountFromContentRange(String contentRange) {
    try {
      Method method = TmfClientCommonUtil.class.getDeclaredMethod("getItemCountFromContentRange", String.class);
      method.setAccessible(true);
      return (int) method.invoke(null, contentRange); // null for static methods
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public static Stream<Arguments> contentRanges() {
    return Stream.of(
        Arguments.of("items 1-20/20", 20),
        Arguments.of("items 1-10/20", 10),
        Arguments.of("items 11-20/20", 10),
        Arguments.of("items 20-20/20", 1),
        Arguments.of("items */20", 0),
        Arguments.of("items */0", 0)
    );
  }

  @ParameterizedTest
      @MethodSource(value = "contentRanges")
  void testGetItemCountFromContentRange(String contentRange, int expectedItemCount) {
    Assertions.assertEquals(expectedItemCount, getItemCountFromContentRange(contentRange));
  }
}
