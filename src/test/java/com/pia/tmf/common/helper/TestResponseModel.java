package com.pia.tmf.common.helper;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestResponseModel {

  private String id;
  private String name;
  private String description;
  private String href;
  private String state;
  private int randomNumber;
  private int orderNumber;
  private List<Characteristic> characteristics;
  private boolean even;
  private OffsetDateTime createdDate;
  private String createdBy;
  private OffsetDateTime updatedDate;
  private String updatedBy;

  @Override
  public String toString() {
    return "TestResponseModel{"
        + "id='"
        + id
        + '\''
        + ", randomNumber="
        + randomNumber
        + ", orderNumber="
        + orderNumber
        + ", even="
        + even
        + '}';
  }

  @Getter
  @Setter
  public static class Characteristic {
    private String key;
    private String value;
  }
}
