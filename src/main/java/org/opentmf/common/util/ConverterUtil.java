package org.opentmf.common.util;

import lombok.experimental.UtilityClass;

/**
 * @author Gokhan Demir
 */
@UtilityClass
public class ConverterUtil {

  public static int toInt(String s, int defaultValue) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }
}
