package com.example.woowa.order.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@AllArgsConstructor
public class ReviewUpdateRequest {
  @NotBlank
  @Length(min = 10, max = 500)
  private final String content;
  @Min(value = 1)
  @Max(value = 5)
  private final Integer scoreType;
}
