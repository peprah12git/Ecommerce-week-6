package com.smartcommerce.service.imp;

import com.smartcommerce.dtos.response.ReviewResponse;
import com.smartcommerce.repositories.ReviewRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
@Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProductId(int productId) {

        return reviewRepository
                .findByProduct_ProductIdOrderByReviewDateDesc(productId)
                .stream()
                .map(review -> {
                    ReviewResponse dto = new ReviewResponse();
                    dto.setReviewId(review.getReviewId());
                    dto.setComment(review.getComment());
                    dto.setRating(review.getRating());
                    dto.setReviewDate(LocalDate.from(review.getReviewDate().toLocalDateTime()));
                    dto.setUserId(review.getUser().getUserId());
                    dto.setUsername(review.getUser().getName());

                    return dto;
                })
                .toList();
    }
}
