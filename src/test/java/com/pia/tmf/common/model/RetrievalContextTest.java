package com.pia.tmf.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class RetrievalContextTest {
  @Test
  void testSetAndGetFields() {
    Set<String> fields = new HashSet<>(Arrays.asList("field1", "field2"));
    RetrievalContext retrievalContext = RetrievalContext.builder().withFields(fields).build();
    assertEquals(fields, retrievalContext.getFields());
    assertNull(retrievalContext.getJsonFilter());
    assertNull(retrievalContext.getJsonFilterQuery());
    assertNull(retrievalContext.getJsonFilterType());
  }

  @Test
  void testSetFieldsWithArray() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    RetrievalContext retrievalContext =
        RetrievalContext.builder().withFields("fields1", "fields2").build();
    assertTrue(fields.containsAll(retrievalContext.getFields()));
  }

  @Test
  void testSetFieldsWithArray_buildWithFieldsMethodUsingStringArray() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    String query = "query";
    JsonFilter.TYPE type = JsonFilter.TYPE.CLIENT;
    RetrievalContext retrievalContext =
        RetrievalContext.builder()
            .withClientJsonFilter(query)
            .withFields("fields1", "fields2")
            .build();
    assertTrue(fields.containsAll(retrievalContext.getFields()));
    assertEquals(type, retrievalContext.getJsonFilterType());
    assertEquals(query, retrievalContext.getJsonFilterQuery());
  }

  @Test
  void testSetFieldsWithArray_buildWithFieldsMethod() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    String query = "query";
    JsonFilter.TYPE type = JsonFilter.TYPE.CLIENT;
    RetrievalContext retrievalContext =
        RetrievalContext.builder()
            .withClientJsonFilter(query)
            .withFields("fields1", "fields2")
            .build();
    assertTrue(fields.containsAll(retrievalContext.getFields()));
    assertEquals(type, retrievalContext.getJsonFilterType());
    assertEquals(query, retrievalContext.getJsonFilterQuery());
  }

  @Test
  void testSetJsonFilter_buildWithJsonFilterMethodMultipleParameter() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    String query = "query";
    JsonFilter.TYPE type = JsonFilter.TYPE.CLIENT;
    RetrievalContext retrievalContext =
        RetrievalContext.builder().withFields(fields).withClientJsonFilter(query).build();
    assertTrue(fields.containsAll(retrievalContext.getFields()));
    assertEquals(type, retrievalContext.getJsonFilterType());
    assertEquals(query, retrievalContext.getJsonFilterQuery());
  }

  @Test
  void testSetJsonFilter_buildWithJsonFilterMethodFilterParameter() {
    Set<String> fields = new HashSet<>(Arrays.asList("fields1", "fields2"));
    String query = "query";
    JsonFilter.TYPE type = JsonFilter.TYPE.CLIENT;
    RetrievalContext retrievalContext =
        RetrievalContext.builder().withFields(fields).withClientJsonFilter(query).build();
    assertTrue(fields.containsAll(retrievalContext.getFields()));
    assertEquals(type, retrievalContext.getJsonFilterType());
    assertEquals(query, retrievalContext.getJsonFilterQuery());
  }

  @Test
  void testSetAndGetJsonFilter() {
    JsonFilter jsonFilter = JsonFilter.of("query", JsonFilter.TYPE.CLIENT);
    RetrievalContext retrievalContext =
        RetrievalContext.builder().withClientJsonFilter("query").build();
    assertEquals(jsonFilter.getQuery(), retrievalContext.getJsonFilter().getQuery());
    assertEquals(jsonFilter.getType(), retrievalContext.getJsonFilter().getType());
  }

  @Test
  void testSetAndGetJsonFilterQuery() {
    String query = "query";
    RetrievalContext retrievalContext =
        RetrievalContext.builder().withClientJsonFilter(query).build();
    assertEquals(query, retrievalContext.getJsonFilterQuery());
  }

  @Test
  void testGetJsonFilterType() {
    JsonFilter jsonFilter = JsonFilter.of("query", JsonFilter.TYPE.CLIENT);
    RetrievalContext retrievalContext = new RetrievalContext();
    retrievalContext.setJsonFilter(jsonFilter);
    assertEquals(JsonFilter.TYPE.CLIENT, retrievalContext.getJsonFilterType());
  }
}
