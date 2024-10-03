package com.pia.tmf.common.model;

import java.util.Set;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.MultiValueMap;

/**
 * Represents a custom offset-based pagination request with filtering and sorting capabilities. This
 * class extends AbstractOffsetRequest and provides additional functionality for filtering and
 * sorting data.
 *
 * @author Yusuf BOZKURT
 */
@Getter
public class TmfOffsetRequest extends AbstractOffsetRequest {
  /** The filtering configuration for the request. */
  private RetrievalContext retrievalContext;

  /**
   * Constructs a new TmfOffsetRequest object with the provided offset, limit, and sorting configuration.
   * @param offset The offset index.
   * @param limit The maximum number of items per page.
   * @param sort The sorting configuration.
   */
  private TmfOffsetRequest(long offset, int limit, Sort sort) {
    this(offset, limit, sort, null);
  }

  /**
   * Constructs a new TmfOffsetRequest object with the provided offset, limit, sorting
   * configuration, and filter.
   *
   * @param offset The offset index.
   * @param limit The maximum number of items per page.
   * @param sort The sorting configuration.
   * @param retrievalContext The filtering configuration.
   */
  private TmfOffsetRequest(long offset, int limit, Sort sort, RetrievalContext retrievalContext) {
    super(sort, offset, limit);
    this.retrievalContext = retrievalContext == null ? new RetrievalContext() : retrievalContext;
  }

  /**
   * Generates a new TmfOffsetRequest object representing the next page.
   * @return A new TmfOffsetRequest object representing the next page.
   */
  @Override
  public Pageable next() {
    return new TmfOffsetRequest(
        this.getOffset() + this.getPageSize(),
        this.getPageSize(),
        this.getSort(),
        this.retrievalContext);
  }

  /**
   * Generates a new TmfOffsetRequest object representing the previous page if it exists; otherwise, returns itself.
   * @return A new TmfOffsetRequest object representing the previous page if it exists; otherwise, itself.
   */
  @Override
  public Pageable previous() {
    return !hasPrevious()
        ? this
        : new TmfOffsetRequest(
            this.getOffset() - this.getPageSize(),
            this.getPageSize(),
            this.getSort(),
            this.retrievalContext);
  }

  /**
   * Generates a new TmfOffsetRequest object representing the first page.
   * @return A new TmfOffsetRequest object representing the first page.
   */
  @Override
  public Pageable first() {
    return new TmfOffsetRequest(0L, this.getPageSize(), this.getSort(), this.retrievalContext);
  }

  /**
   * Generates a new TmfOffsetRequest object with the specified page number.
   * @param pageNumber The page number.
   * @return A new TmfOffsetRequest object with the specified page number.
   */
  @Override
  public Pageable withPage(int pageNumber) {
    return new TmfOffsetRequest(
        ((long) getPageSize() * pageNumber), getPageSize(), getSort(), this.retrievalContext);
  }


  /**
   * Retrieves the fields included or excluded in the filtering configuration.
   * @return The fields included or excluded in the filtering configuration.
   */
  public Set<String> getFields() {
    return this.retrievalContext.getFields();
  }

  /**
   * Retrieves the JSON filter included in the filtering configuration.
   * @return The JSON filter included in the filtering configuration.
   */
  public JsonFilter getJsonFilter() {
    return this.retrievalContext.getJsonFilter();
  }

  public MultiValueMap<String, String> getHeaders() {
    return this.retrievalContext.getHeaderParameters();
  }

  /**
   * Retrieves the query string of the JSON filter included in the filtering configuration.
   * @return The query string of the JSON filter included in the filtering configuration.
   */
  public String getJsonFilterQuery() {
    return this.retrievalContext.getJsonFilterQuery();
  }

  /**
   * Retrieves the type of the JSON filter included in the filtering configuration.
   * @return The type of the JSON filter included in the filtering configuration.
   */
  public JsonFilter.TYPE getJsonFilterTYpe() {
    return this.retrievalContext.getJsonFilterType();
  }

  /**
   * Creates a new TmfOffsetRequest object with default settings.
   * @return A new TmfOffsetRequest object with default settings.
   */
  public static TmfOffsetRequest of() {
    return of(0, Integer.MAX_VALUE, Sort.unsorted());
  }

  /**
   * Creates a new TmfOffsetRequest object with the specified offset and default settings.
   * @param offset The offset index.
   * @return A new TmfOffsetRequest object with the specified offset and default settings.
   */
  public static TmfOffsetRequest of(int offset) {
    return of(offset, Integer.MAX_VALUE, Sort.unsorted());
  }

  /**
   * Creates a new TmfOffsetRequest object with the specified offset, limit, and default settings.
   * @param offset The offset index.
   * @param limit The maximum number of items per page.
   * @return A new TmfOffsetRequest object with the specified offset, limit, and default settings.
   */
  public static TmfOffsetRequest of(int offset, int limit) {
    return of(offset, limit, Sort.unsorted());
  }

  /**
   * Creates a new TmfOffsetRequest object with the specified sorting direction and properties.
   * @param direction The sorting direction.
   * @param properties The properties to sort by.
   * @return A new TmfOffsetRequest object with the specified sorting direction and properties.
   */
  public static TmfOffsetRequest of(Sort.Direction direction, String... properties) {
    return of(0, Integer.MAX_VALUE, Sort.by(direction, properties));
  }

  /**
   * Creates a new TmfOffsetRequest object with the specified offset, limit, sorting direction, and properties.
   * @param offset The offset index.
   * @param limit The maximum number of items per page.
   * @param direction The sorting direction.
   * @param properties The properties to sort by.
   * @return A new TmfOffsetRequest object with the specified offset, limit, sorting direction, and properties.
   */
  public static TmfOffsetRequest of(int offset, int limit, Sort.Direction direction, String... properties) {
    return of(offset, limit, Sort.by(direction, properties));
  }

  /**
   * Creates a new TmfOffsetRequest object with the specified offset, limit, and sorting configuration.
   * @param offset The offset index.
   * @param limit The maximum number of items per page.
   * @param sort The sorting configuration.
   * @return A new TmfOffsetRequest object with the specified offset, limit, and sorting configuration.
   */
  public static TmfOffsetRequest of(int offset, int limit, Sort sort) {
    return new TmfOffsetRequest(offset, limit, sort);
  }

  /**
   * Updates the fields included or excluded in the filtering configuration.
   * @param fields The fields to include or exclude.
   * @return This TmfOffsetRequest object with updated filtering configuration.
   */
  public TmfOffsetRequest withFields(String... fields) {
    this.retrievalContext.setFields(Set.of(fields));
    return this;
  }

  /**
   * Sets the previously configured RetrievalContext.
   * @param retrievalContext the retrieval context.
   * @return This TmfOffsetRequest object with updated RetrievalContext.
   */
  public TmfOffsetRequest withRetrievalContext(RetrievalContext retrievalContext) {
    this.retrievalContext = retrievalContext;
    return this;
  }

  /**
   * Sets a client-side JSON filter with the provided query string.
   * @param query The query string for the client-side JSON filter.
   * @return This TmfOffsetRequest object with the updated client-side JSON filter.
   */
  public TmfOffsetRequest withClientFilter(String query) {
    this.retrievalContext.setJsonFilter(JsonFilter.of(query, JsonFilter.TYPE.CLIENT));
    return this;
  }

  /**
   * Sets a server-side JSON filter with the provided query string.
   * @param query The query string for the server-side JSON filter.
   * @return This TmfOffsetRequest object with the updated server-side JSON filter.
   */
  public TmfOffsetRequest withServerFilter(String query) {
    this.retrievalContext.setJsonFilter(JsonFilter.of(query, JsonFilter.TYPE.SERVER));
    return this;
  }

  /**
   * Sets the limit for the number of items per page.
   * @param limit The limit for the number of items per page.
   * @return A new TmfOffsetRequest object with the specified limit.
   */
  public TmfOffsetRequest withLimit(Integer limit) {
    return new TmfOffsetRequest(this.getOffset(), limit, this.getSort(), this.retrievalContext);
  }

  public TmfOffsetRequest withHeaderParameters(MultiValueMap<String, String> headerParameters) {
    this.retrievalContext.setHeaderParameters(headerParameters);
    return this;
  }
}
