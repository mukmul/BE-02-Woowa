package com.example.woowa.order.review.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.example.woowa.customer.customer.entity.Customer;
import com.example.woowa.customer.customer.entity.CustomerGrade;
import com.example.woowa.customer.customer.service.CustomerService;
import com.example.woowa.delivery.entity.Delivery;
import com.example.woowa.delivery.enums.DeliveryStatus;
import com.example.woowa.order.order.entity.Order;
import com.example.woowa.order.order.service.OrderService;
import com.example.woowa.order.review.dto.ReviewCreateRequest;
import com.example.woowa.order.review.dto.ReviewFindResponse;
import com.example.woowa.order.review.dto.ReviewUpdateRequest;
import com.example.woowa.order.review.entity.Review;
import com.example.woowa.order.review.enums.ReviewStatus;
import com.example.woowa.order.review.enums.ScoreType;
import com.example.woowa.order.review.repository.ReviewRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private Order order;

    @MockitoBean
    private Delivery delivery;

    @MockitoBean
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("리뷰 생성")
    void createReview() {

        CustomerGrade customerGrade = new CustomerGrade(5, "일반", 3000, 2);
        Customer customer = new Customer("test1234", "Test1234!", LocalDate.of(2000, 1, 1), customerGrade);
        Review review = new Review("정말정말 맛있습니다.", ScoreType.FIVE, null, null);
        review.setReviewStatus(ReviewStatus.REGISTERED);

        given(customerService.findCustomerEntity(anyString())).willReturn(customer);
        given(delivery.getDeliveryStatus()).willReturn(DeliveryStatus.DELIVERY_FINISH);
        given(order.getDelivery()).willReturn(delivery);
        given(order.getCustomer()).willReturn(customer);
        given(orderService.findOrderById(anyLong())).willReturn(order);
        given(reviewRepository.save(any())).willReturn(review);

        ReviewCreateRequest reviewCreateRequest = new ReviewCreateRequest("정말정말 맛있습니다.", 5);
        ReviewFindResponse reviewFindResponse = reviewService.createReview("test1234", 1L, reviewCreateRequest);

        Assertions.assertThat(reviewFindResponse.getContent()).isEqualTo("정말정말 맛있습니다.");
        Assertions.assertThat(reviewFindResponse.getScoreType()).isEqualTo(ScoreType.FIVE.getValue());
    }

    @Test
    @DisplayName("리뷰 조회")
    void findReview() {

        Review review = new Review("정말정말 맛있습니다.",
                ScoreType.FIVE, null, null);
        review.setReviewStatus(ReviewStatus.REGISTERED);

        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        // 정상적으로 조회가 되는 경우
        ReviewFindResponse reviewFindResponse = reviewService.findReview(1L);

        Assertions.assertThat(reviewFindResponse.getContent()).isEqualTo("정말정말 맛있습니다.");
        Assertions.assertThat(reviewFindResponse.getScoreType()).isEqualTo(ScoreType.FIVE.getValue());

        // 존재하지 않는 리뷰 id 를 조회하는 경우
        Assertions.assertThatThrownBy(() -> reviewService.findReview(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("review not existed");
    }

    @Test
    @DisplayName("유저 리뷰 목록 조회")
    void findUserReview() {
        Review review = new Review("정말정말 맛있습니다.",
                ScoreType.FIVE, null, null);
        review.setReviewStatus(ReviewStatus.REGISTERED);

        CustomerGrade customerGrade = new CustomerGrade(5, "일반", 3000, 2);
        Customer customer = new Customer("dev12", "Programmers123!", LocalDate.of(2000, 1, 1), customerGrade);
        customer.addReview(review);
        given(customerService.findCustomerEntity(anyString())).willReturn(customer);

        List<ReviewFindResponse> reviews = reviewService.findUserReview("dev12");

        Assertions.assertThat(reviews.getFirst().getContent()).isEqualTo("정말정말 맛있습니다.");
        Assertions.assertThat(reviews.getFirst().getScoreType()).isEqualTo(ScoreType.FIVE.getValue());
    }

    @Test
    @DisplayName("리뷰 작성자 검증")
    void validateReviewWriter() {
        CustomerGrade customerGrade = new CustomerGrade(5, "일반", 3000, 2);
        Customer customer = new Customer("test1234", "Test1234!", LocalDate.of(2000, 1, 1), customerGrade);

        Review review = new Review("정말정말 맛있습니다.",
                ScoreType.FIVE, customer, null);
        review.setReviewStatus(ReviewStatus.REGISTERED);

        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest("정말정말 맛없습니다.", 1);

        // 자신이 작성하지 않은 리뷰인 경우 예외
        Assertions.assertThatThrownBy(() -> reviewService.updateReview("abc1234", 1L, reviewUpdateRequest))
                .isInstanceOf(RuntimeException.class) // 예외 타입 검증
                .hasMessage("This user is not a review writer"); // 예외 메시지 검증
    }

    @Test
    @DisplayName("리뷰 업데이트")
    void updateReview() {
        CustomerGrade customerGrade = new CustomerGrade(5, "일반", 3000, 2);
        Customer customer = new Customer("test1234", "Test1234!", LocalDate.of(2000, 1, 1), customerGrade);

        Review review = new Review("정말정말 맛있습니다.",
                ScoreType.FIVE, customer, null);
        review.setReviewStatus(ReviewStatus.REGISTERED);

        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        ReviewUpdateRequest reviewUpdateRequest = new ReviewUpdateRequest("정말정말 맛없습니다.", 1);

        ReviewFindResponse reviewFindResponse = reviewService.updateReview("test1234", 1L, reviewUpdateRequest);

        Assertions.assertThat(reviewFindResponse).isNotNull();

        Assertions.assertThat(reviewFindResponse.getContent()).isEqualTo("정말정말 맛없습니다.");
        Assertions.assertThat(review.getReviewStatus()).isEqualTo(ReviewStatus.EDITED);
        Assertions.assertThat(reviewFindResponse.getScoreType()).isEqualTo(ScoreType.ONE.getValue());
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview() {
        CustomerGrade customerGrade = new CustomerGrade(5, "일반", 3000, 2);
        Customer customer = new Customer("test1234", "Test1234!", LocalDate.of(2000, 1, 1), customerGrade);

        Review review = new Review("정말정말 맛있습니다.",
                ScoreType.FIVE, customer, null);
        review.setReviewStatus(ReviewStatus.REGISTERED);

        customer.addReview(review);

        given(customerService.findCustomerEntity(anyString())).willReturn(customer);
        given(reviewRepository.findById(anyLong())).willReturn(Optional.of(review));

        reviewService.deleteReview("test1234", 1L);

//        verify(reviewRepository).delete(review);
        Assertions.assertThat(review.getReviewStatus()).isEqualTo(ReviewStatus.DELETED);
    }
}
