package org.opentmf.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.opentmf.common.config.TmfClientConfigurations.TmfClientConfig;
import org.opentmf.common.model.TmfRequestContext;

/**
 * @author Gokhan Demir
 */
class TmfClientCommonUtilTests {

  private static final String COMPOSITE_ID = "PhysicalSimResourceSpecification:(version=1)";
  private static final String EXPECTED_ENCODED_ID =
      "PhysicalSimResourceSpecification%3A%28version%3D1%29";

  private static int getItemCountFromContentRange(String contentRange) {
    try {
      Method method = TmfClientCommonUtil.class.getDeclaredMethod("getItemCountFromContentRange", String.class);
      method.setAccessible(true);
      return (int) method.invoke(null, contentRange); // null for static methods
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static URI invokeUpdateUri(URI uri, TmfRequestContext ctx) {
    try {
      Method method = TmfClientCommonUtil.class.getDeclaredMethod(
          "updateUri", URI.class, TmfRequestContext.class);
      method.setAccessible(true);
      return (URI) method.invoke(null, uri, ctx);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  private static TmfClientConfig config() {
    var cfg = new TmfClientConfig();
    cfg.setBaseUrl("http://example.com");
    cfg.setContextPath("/tmf-api/v4");
    cfg.setEndpoint("/resourceSpecification");
    return cfg;
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

  @Test
  void buildUriWithId_compositeKey_encodesIdOnce() {
    URI uri = TmfClientCommonUtil.buildUriWithId(config(), COMPOSITE_ID);
    assertThat(uri.toString()).endsWith("/" + EXPECTED_ENCODED_ID);
    assertThat(uri.toString()).doesNotContain("%25");
  }

  @Test
  void buildUriWithId_compositeKey_withCtx_encodesIdOnce() {
    TmfRequestContext ctx = TmfRequestContext.builder().build();
    URI uri = TmfClientCommonUtil.buildUriWithId(config(), COMPOSITE_ID, ctx);
    assertThat(uri.toString()).endsWith("/" + EXPECTED_ENCODED_ID);
    assertThat(uri.toString()).doesNotContain("%25");
  }

  @Test
  void updateUri_emptyCtx_doesNotReEncodePath() {
    URI base = TmfClientCommonUtil.buildUriWithId(config(), COMPOSITE_ID);
    URI result = invokeUpdateUri(base, TmfRequestContext.builder().build());
    assertThat(result.toString()).endsWith("/" + EXPECTED_ENCODED_ID);
    assertThat(result.toString()).doesNotContain("%25");
  }

  @Test
  void updateUri_ctxWithFieldsAndFilter_preservesPathEncoding() {
    URI base = TmfClientCommonUtil.buildUriWithId(config(), COMPOSITE_ID);
    TmfRequestContext ctx = TmfRequestContext.builder()
        .withServerJsonFilter("name=='x'")
        .withFields("id", "name")
        .build();
    URI result = invokeUpdateUri(base, ctx);
    assertThat(result.toString()).contains("/" + EXPECTED_ENCODED_ID);
    assertThat(result.toString()).contains("filter=").contains("fields=");
    assertThat(result.toString()).doesNotContain("%25");
  }
}
