package com.pia.tmf.common.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

class TmfOffsetRequestTest {

  @Test
  void next() {
    Pageable request = TmfOffsetRequest.of(0, 10);
    Pageable next = request.next();

    assertEquals(10, next.getOffset());
    assertEquals(10, next.getPageSize());
  }

  @Test
  void previous() {
    AbstractOffsetRequest request = TmfOffsetRequest.of(10, 10);
    AbstractOffsetRequest previous = (TmfOffsetRequest) request.previous();

    assertEquals(0, previous.getOffset());
    assertEquals(10, previous.getPageSize());
  }

  @Test
  void previousIfFirstPage() {
    AbstractOffsetRequest request = TmfOffsetRequest.of(0, 10);
    AbstractOffsetRequest previous = (TmfOffsetRequest) request.previous();

    assertEquals(0, previous.getOffset());
    assertEquals(10, previous.getPageSize());
  }

  @Test
  void first() {
    Pageable request = TmfOffsetRequest.of(10, 10);
    Pageable first = request.first();

    assertEquals(0, first.getOffset());
    assertEquals(10, first.getPageSize());
  }

  @Test
  void withPage() {
    Pageable request = TmfOffsetRequest.of(0, 10);
    Pageable withPage = request.withPage(2);

    assertEquals(20, withPage.getOffset());
    assertEquals(10, withPage.getPageSize());
  }

  @Test
  void withEverything() {
    RetrievalContext retrievalContext = RetrievalContext.builder()
        .withFields("id", "href", "version")
        .withHeaderValues("header1", "value1")
        .withHeaderValues("header2", "value2")
        .withServerJsonFilter("$.attachment[?(@.size==300)]")
        .build();
    TmfOffsetRequest request = TmfOffsetRequest.of(0, 10,
            Sort.by(Order.asc("name"), Order.desc("surname")))
        .withRetrievalContext(retrievalContext);
    Assertions.assertNotNull(request);
  }

  @Test
  void withFields() {
    TmfOffsetRequest request = TmfOffsetRequest.of(0, 10);
    TmfOffsetRequest withFields = request.withFields("test");

    assertEquals(Set.of("test"), withFields.getRetrievalContext().getFields());
  }

  @Test
  void withClientsFilter() {
    TmfOffsetRequest request = TmfOffsetRequest.of(0, 10);
    TmfOffsetRequest withFilter = request.withClientFilter("test");

    assertEquals("test", withFilter.getRetrievalContext().getJsonFilter().getQuery());
    assertEquals(
        JsonFilter.TYPE.CLIENT, withFilter.getRetrievalContext().getJsonFilter().getType());
  }

  @Test
  void withServerFilter() {
    TmfOffsetRequest request = TmfOffsetRequest.of(0, 10);
    TmfOffsetRequest withFilter = request.withServerFilter("test");

    assertEquals("test", withFilter.getJsonFilterQuery());
    assertEquals(JsonFilter.TYPE.SERVER, withFilter.getJsonFilterTYpe());
  }

  @Test
  void withLimit() {
    TmfOffsetRequest request = TmfOffsetRequest.of(0, 10);
    TmfOffsetRequest withLimit = request.withLimit(20);

    assertEquals(20, withLimit.getPageSize());
  }

  @Test
  void of() {
    Pageable request = TmfOffsetRequest.of(0, 10, Sort.Direction.ASC, "test");

    assertEquals(0, request.getOffset());
    assertEquals(10, request.getPageSize());
    assertEquals(
        Sort.Direction.ASC,
        Objects.requireNonNull(request.getSort().getOrderFor("test")).getDirection());
  }

  @Test
  void ofNullSort() {
    Pageable request = TmfOffsetRequest.of(0, 10, null);

    assertEquals(0, request.getOffset());
    assertEquals(10, request.getPageSize());
    assertEquals(Sort.unsorted(), request.getSort());
  }

  @Test
  void getPageSize() {
    AbstractOffsetRequest request = TmfOffsetRequest.of(0, 10);
    assertEquals(10, request.getPageSize());
  }

  @Test
  void getPageNumber() {
    AbstractOffsetRequest request = TmfOffsetRequest.of(20, 10);
    assertEquals(2, request.getPageNumber());
  }

  @Test
  void hasPrevious() {
    AbstractOffsetRequest request = TmfOffsetRequest.of(20, 10);
    assertTrue(request.hasPrevious());

    request = TmfOffsetRequest.of(0, 10);
    assertFalse(request.hasPrevious());
  }

  @Test
  void getOffset() {
    AbstractOffsetRequest request = TmfOffsetRequest.of(20, 10);
    assertEquals(20, request.getOffset());
  }

  @Test
  void previousOrFirst() {
    AbstractOffsetRequest request = TmfOffsetRequest.of(20, 10);
    AbstractOffsetRequest previousOrFirst = (TmfOffsetRequest) request.previousOrFirst();

    assertEquals(10, previousOrFirst.getOffset());
    assertEquals(10, previousOrFirst.getPageSize());

    request = TmfOffsetRequest.of(0, 10);
    previousOrFirst = (TmfOffsetRequest) request.previousOrFirst();

    assertEquals(0, previousOrFirst.getOffset());
    assertEquals(10, previousOrFirst.getPageSize());
  }

  @Test
  void constructorShouldThrowExceptionWhenOffsetIsNegative() {
    assertThrows(IllegalArgumentException.class, () -> TmfOffsetRequest.of(-1, 10));
  }

  @Test
  void constructorShouldThrowExceptionWhenLimitIsLessThanOne() {
    assertThrows(IllegalArgumentException.class, () -> TmfOffsetRequest.of(0, 0));
  }
}
