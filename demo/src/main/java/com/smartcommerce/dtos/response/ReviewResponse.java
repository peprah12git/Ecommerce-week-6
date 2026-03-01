package com.smartcommerce.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private int reviewId;
    private String comment;
    private int rating;
    private LocalDate reviewDate;
    private int userId;
    private String username;

}
