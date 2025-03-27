package com.pia.tmf.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TmfRequestContextTest {
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
