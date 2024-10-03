package com.pia.tmf.common.model;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Represents an abstract offset-based pagination request.
 * This class provides functionality to handle pagination based on offset and limit.
 * @author Yusuf BOZKURT
 */
public abstract class AbstractOffsetRequest implements Pageable {
  /** The sorting configuration for the request. */
  private final Sort sort;

  /** The offset index. */
  private final long offset;
  /** The maximum number of items per page. */
  private final int limit;

  /**
   * Constructs a new AbstractOffsetRequest object with the provided offset and limit.
   *
   * @param offset The offset index.
   * @param limit The maximum number of items per page.
   * @throws IllegalArgumentException if the offset is less than zero or if the limit is less than
   *     one.
   */
  protected AbstractOffsetRequest(Sort sort, long offset, int limit) {
    if (offset < 0) {
      throw new IllegalArgumentException("Offset index must not be less than zero!");
    }
    if (limit < 1) {
        throw new IllegalArgumentException("Limit must not be less than one!");
    }
    this.sort = sort == null ? Sort.unsorted() : sort;
    this.offset = offset;
    this.limit = limit;
  }

  /**
   * Retrieves the sorting configuration for the request.
   *
   * @return The sorting configuration for the request.
   */
  @Override
  public Sort getSort() {
    return this.sort;
  }

  /**
   * Retrieves the maximum number of items per page.
   * @return The maximum number of items per page.
   */
  @Override
  public int getPageSize() {
    return this.limit;
  }

  /**
   * Retrieves the zero-based page index.
   * @return The zero-based page index.
   */
  @Override
  public int getPageNumber() {
    return Math.toIntExact(offset / this.limit);
  }

  /**
   * Checks if there is a previous page.
   * @return true if there is a previous page, false otherwise.
   */
  @Override
  public boolean hasPrevious() {
    return getPageNumber() > 0;
  }

  /**
   * Retrieves the offset index.
   * @return The offset index.
   */
  @Override
  public long getOffset() {
    return this.offset;
  }

  public Pageable previousOrFirst() {
    return this.hasPrevious() ? this.previous() : this.first();
  }

  /**
   * Returns the previous page if it exists; otherwise, returns the first page.
   * @return The previous page if it exists; otherwise, the first page.
   */
  public abstract Pageable previous();


}
