package org.opentmf.common.model;

import org.springframework.data.domain.Pageable;

/**
 * Represents an implementation of the TmfPage interface for offset-based pagination.
 * This class calculates and provides metadata about pagination, such as total pages, total elements, content, page size, current page number,
 * and whether there is a next page or if the current page is the last one.
 * Additionally, it offers a method to obtain the pageable object for the next page.
 * @param <T> The type of content in the page.
 * @author Yusuf BOZKURT
 */
public class OffsetPage<T> implements TmfPage<T> {

  /** The pageable object associated with the page. */
  private final Pageable tmfPageable;

  /** The total number of elements across all pages. */
  private final long total;

  /** The size of the page. */
  private final int size;

  /** The content of the page. */
  private final T content;

  /**
   * Constructs a new OffsetPage object with the provided total number of elements, page size, pageable object, and content.
   * @param total The total number of elements across all pages.
   * @param itemCount The size of the current page.
   * @param tmfPageable The pageable object representing the current page.
   * @param content The content of the page.
   */
  public OffsetPage(long total, int itemCount, Pageable tmfPageable, T content) {
    this.total = total;
    this.size = itemCount;

    // Determine the limit for the pageable object
    int pageableLimit = tmfPageable.getPageNumber() == 0 && itemCount != tmfPageable.getPageSize() && itemCount > 0
            ? itemCount
            : tmfPageable.getPageSize();

    // Adjust the pageable object based on whether it's a TmfOffsetRequest or not
    if(tmfPageable instanceof TmfOffsetRequest tmfOffsetRequest) {
      this.tmfPageable = tmfOffsetRequest.withLimit(pageableLimit);
    } else {
      this.tmfPageable = TmfOffsetRequest.of((int) tmfPageable.getOffset(), pageableLimit, tmfPageable.getSort());
    }

    this.content = content;
  }

  /**
   * Retrieves the total number of pages.
   * @return The total number of pages.
   */
  public int getTotalPages() {
    int pageLimit = tmfPageable.getPageSize();
    int totalPages = (int) (total / pageLimit);
    if (total % pageLimit != 0) {
      totalPages++;
    }
    return totalPages;
  }

  /**
   * Retrieves the total number of elements across all pages.
   * @return The total number of elements.
   */
  @Override
  public long getTotalElements() {
    return total;
  }

  /**
   * Retrieves the content of the page.
   * @return The content of the page.
   */
  @Override
  public T getContent() {
    return content;
  }

  /**
   * Retrieves the size of the page.
   * @return The size of the page.
   */
  @Override
  public int getSize() {
    return this.size;
  }

  /**
   * Retrieves the current page number.
   * @return The current page number.
   */
  public int getNumber() {
    return tmfPageable.getPageNumber();
  }

  /**
   * Checks if there is a next page available.
   * @return true if there is a next page, false otherwise.
   */
  public boolean hasNext() {
    return getNumber() + 1 < getTotalPages();
  }

  /**
   * Checks if the current page is the last one.
   * @return true if the current page is the last one, false otherwise.
   */
  public boolean isLast() {
    return !hasNext();
  }

  /**
   * Retrieves the pageable object for the next page.
   * @return The pageable object for the next page.
   */
  @Override
  public Pageable getNextPageable() {
    return this.tmfPageable.next();
  }
}
