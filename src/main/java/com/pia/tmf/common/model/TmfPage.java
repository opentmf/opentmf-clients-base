package com.pia.tmf.common.model;

import org.springframework.data.domain.Pageable;

/**
 * Represents a custom page interface providing metadata about pagination. This interface defines
 * methods to retrieve information about the total number of pages, total elements, content, page
 * size, current page number, and whether there is a next page or if the current page is the last
 * one. Additionally, it provides a method to obtain the pageable object for the next page.
 *
 * @param <T> The type of content in the page.
 * @author Yusuf BOZKURT
 */
public interface TmfPage<T> {

  /**
   * Retrieves the total number of pages.
   *
   * @return The total number of pages.
   */
  int getTotalPages();

  /**
   * Retrieves the total number of elements across all pages.
   *
   * @return The total number of elements.
   */
  long getTotalElements();

  /**
   * Retrieves the content of the page.
   *
   * @return The content of the page.
   */
  T getContent();

  /**
   * Retrieves the size of the page.
   *
   * @return The size of the page.
   */
  int getSize();

  /**
   * Retrieves the current page number.
   *
   * @return The current page number.
   */
  int getNumber();

  /**
   * Checks if there is a next page available.
   *
   * @return true if there is a next page, false otherwise.
   */
  boolean hasNext();

  /**
   * Checks if the current page is the last one.
   *
   * @return true if the current page is the last one, false otherwise.
   */
  boolean isLast();

  /**
   * Retrieves the pageable object for the next page.
   *
   * @return The pageable object for the next page.
   */
  Pageable getNextPageable();
}
