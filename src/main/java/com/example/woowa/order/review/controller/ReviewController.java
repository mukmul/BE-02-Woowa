package com.example.woowa.order.review.controller;

import com.example.woowa.order.review.dto.ReviewCreateRequest;
import com.example.woowa.order.review.dto.ReviewFindResponse;
import com.example.woowa.order.review.dto.ReviewUpdateRequest;
import com.example.woowa.order.review.service.ReviewService;

import java.util.List;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/{orderId}")
    public ReviewFindResponse createReview(@PathVariable Long orderId,
                                           @RequestBody @Valid ReviewCreateRequest reviewCreateRequest,
                                           @AuthenticationPrincipal UserDetails userDetails) {

        String loginId = userDetails.getUsername();
        return reviewService.createReview(loginId, orderId, reviewCreateRequest);
    }

    @GetMapping("/{id}")
    public ReviewFindResponse findReview(@PathVariable Long id) {
        return reviewService.findReview(id);
    }

    @GetMapping("/user/{loginId}")
    public List<ReviewFindResponse> findUserReview(@PathVariable String loginId) {
        return reviewService.findUserReview(loginId);
    }

    @PutMapping("/{id}")
    public ReviewFindResponse updateReview(@AuthenticationPrincipal UserDetails userDetails,
                                           @PathVariable Long id,
                                           @RequestBody @Valid ReviewUpdateRequest reviewUpdateRequest) {

        String loginId = userDetails.getUsername();
        return reviewService.updateReview(loginId, id, reviewUpdateRequest);
    }

    @DeleteMapping("/{id}")
    public String deleteReview(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {

        String loginId = userDetails.getUsername();

        reviewService.deleteReview(loginId, id);
        return "delete id - " + id;
    }
}
