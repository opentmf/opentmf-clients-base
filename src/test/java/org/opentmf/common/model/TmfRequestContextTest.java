package org.opentmf.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

class TmfRequestContextTest {

  public static final String JSON_FILTER = "$.store.book[*].title";

  @Test
  void testSetAndGetFields() {
    Set<String> fields = new HashSet<>(Arrays.asList("field1", "field2"));
    TmfRequestContext requestContext = TmfRequestContext.builder().withFields(fields).build();
    assertEquals(fields, requestContext.getFields());
    assertNull(requestContext.getJsonFilter());
    assertNull(requestContext.getJsonFilterQuery());
    assertNull(requestContext.getJsonFilterType());
  }

  @Test
  void testSetFieldsWithArray() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    TmfRequestContext requestContext =
        TmfRequestContext.builder().withFields("fields1", "fields2").build();
    assertTrue(fields.containsAll(requestContext.getFields()));
  }

  @Test
  void testTmfRequestContextBuilder_withJsonFilter_buildsExpectedObject() {
    var ctx = TmfRequestContext.builder()
            .withClientJsonFilter(JSON_FILTER)
            .build();
    Assertions.assertEquals(JSON_FILTER, ctx.getJsonFilter().getQuery());
    var ctx2 = TmfRequestContext.builder(ctx).build();
    Assertions.assertEquals(JSON_FILTER, ctx2.getJsonFilter().getQuery());
  }

  @Test
  void test_firstServerThenClientJsonFilter_cantBeApplied() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> TmfRequestContext.builder()
            .withServerJsonFilter(JSON_FILTER)
            .withClientJsonFilter(JSON_FILTER));
  }

  @Test
  void test_firstClientThenServerJsonFilter_cantBeApplied() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> TmfRequestContext.builder()
            .withClientJsonFilter(JSON_FILTER)
            .withServerJsonFilter(JSON_FILTER));
  }

  @Test
  void test_withHeaderValues_usingMultiValueMap_constructsAsExpected() {
    var headers = multiValueMap();

    var ctx = TmfRequestContext.builder()
            .withHeaderValues(headers).build();

    Assertions.assertEquals(2, ctx.getHeaderParameters().size());

    for (var entry : ctx.getHeaderParameters().entrySet()) {
      Assertions.assertEquals(2, entry.getValue().size());
    }
  }

  @Test
  void test_withHeaderValues_usingFirstMultiValueMapSecondString_constructsAsExpected() {
    var headers = multiValueMap();
    var ctx = TmfRequestContext.builder()
            .withHeaderValues(headers)
            .withHeaderValues("a", "b", "c")
            .build();

    Assertions.assertEquals(3, ctx.getHeaderParameters().size());

    for (var entry : ctx.getHeaderParameters().entrySet()) {
      Assertions.assertEquals(2, entry.getValue().size());
    }
  }

  @Test
  void test_withHeaderValues_usingFirstStringSecondMultiValueMap_constructsAsExpected() {
    var headers = multiValueMap();
    var ctx = TmfRequestContext.builder()
            .withHeaderValues("a", "b", "c")
            .withHeaderValues(headers)
            .build();

    Assertions.assertEquals(3, ctx.getHeaderParameters().size());

    for (var entry : ctx.getHeaderParameters().entrySet()) {
      Assertions.assertEquals(2, entry.getValue().size());
    }
  }

  @Test
  void testWithFields_firstSetThenString_constructsAsExpected() {
    var ctx = TmfRequestContext.builder()
            .withFields(Set.of("field1", "field2"))
            .withFields("field3")
            .build();

    Assertions.assertEquals(3, ctx.getFields().size());
  }

  @Test
  void testWithFields_firstStringThenSet_constructsAsExpected() {
    var ctx = TmfRequestContext.builder()
            .withFields("field3")
            .withFields(Set.of("field1", "field2"))
            .build();

    Assertions.assertEquals(3, ctx.getFields().size());
  }

  @Test
  void testWithQueryParameters_firstStringThenMultiValueMap_constructsAsExpected() {
    var ctx = TmfRequestContext.builder()
            .withQueryParameters("field3", "value1", "value2")
            .withQueryParameters(multiValueMap())
            .build();

    Assertions.assertEquals(3, ctx.getQueryParameters().size());

    for (var entry : ctx.getQueryParameters().entrySet()) {
      Assertions.assertEquals(2, entry.getValue().size());
    }
  }

  @Test
  void testWithQueryParameters_firstMultiValueMapThenString_constructsAsExpected() {
    var ctx = TmfRequestContext.builder()
            .withQueryParameters(multiValueMap())
            .withQueryParameters("field3", "value1", "value2")
            .build();

    Assertions.assertEquals(3, ctx.getQueryParameters().size());

    for (var entry : ctx.getQueryParameters().entrySet()) {
      Assertions.assertEquals(2, entry.getValue().size());
    }
  }

  private MultiValueMap<String, String> multiValueMap() {
    MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
    multiValueMap.add("param1", "value11");
    multiValueMap.add("param1", "value12");
    multiValueMap.add("param2", "value21");
    multiValueMap.add("param2", "value22");
    return multiValueMap;
  }

  @Test
  void testSetFieldsWithArray_buildWithFieldsMethodUsingStringArray() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    String query = "query";
    JsonFilter.TYPE type = JsonFilter.TYPE.CLIENT;
    TmfRequestContext requestContext =
        TmfRequestContext.builder()
            .withClientJsonFilter(query)
            .withFields("fields1", "fields2")
            .build();
    assertTrue(fields.containsAll(requestContext.getFields()));
    assertEquals(type, requestContext.getJsonFilterType());
    assertEquals(query, requestContext.getJsonFilterQuery());
  }

  @Test
  void testSetFieldsWithArray_buildWithFieldsMethod() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    String query = "query";
    JsonFilter.TYPE type = JsonFilter.TYPE.CLIENT;
    TmfRequestContext requestContext =
        TmfRequestContext.builder()
            .withClientJsonFilter(query)
            .withFields("fields1", "fields2")
            .build();
    assertTrue(fields.containsAll(requestContext.getFields()));
    assertEquals(type, requestContext.getJsonFilterType());
    assertEquals(query, requestContext.getJsonFilterQuery());
  }

  @Test
  void testSetJsonFilter_buildWithJsonFilterMethodMultipleParameter() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    String query = "query";
    JsonFilter.TYPE type = JsonFilter.TYPE.CLIENT;
    TmfRequestContext requestContext =
        TmfRequestContext.builder().withFields(fields).withClientJsonFilter(query).build();
    assertTrue(fields.containsAll(requestContext.getFields()));
    assertEquals(type, requestContext.getJsonFilterType());
    assertEquals(query, requestContext.getJsonFilterQuery());
  }

  @Test
  void testSetJsonFilter_buildWithJsonFilterMethodFilterParameter() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    String query = "query";
    JsonFilter.TYPE type = JsonFilter.TYPE.CLIENT;
    TmfRequestContext requestContext =
        TmfRequestContext.builder().withFields(fields).withClientJsonFilter(query).build();
    assertTrue(fields.containsAll(requestContext.getFields()));
    assertEquals(type, requestContext.getJsonFilterType());
    assertEquals(query, requestContext.getJsonFilterQuery());
  }

  @Test
  void testSetAndGetJsonFilter() {
    JsonFilter jsonFilter = JsonFilter.of("query", JsonFilter.TYPE.CLIENT);
    TmfRequestContext requestContext =
        TmfRequestContext.builder().withClientJsonFilter("query").build();
    assertEquals(jsonFilter.getQuery(), requestContext.getJsonFilter().getQuery());
    assertEquals(jsonFilter.getType(), requestContext.getJsonFilter().getType());
  }

  @Test
  void testSetAndGetJsonFilterQuery() {
    String query = "query";
    TmfRequestContext requestContext =
        TmfRequestContext.builder().withClientJsonFilter(query).build();
    assertEquals(query, requestContext.getJsonFilterQuery());
  }

  @Test
  void testGetJsonFilterType() {
    JsonFilter jsonFilter = JsonFilter.of("query", JsonFilter.TYPE.CLIENT);
    TmfRequestContext requestContext = new TmfRequestContext();
    requestContext.setJsonFilter(jsonFilter);
    assertEquals(JsonFilter.TYPE.CLIENT, requestContext.getJsonFilterType());
  }
}
