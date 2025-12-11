package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Review.ReviewModifyRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewResponseDTO;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;

    /* 1. 리뷰 코드로 상세 조회 */
    public ReviewResponseDTO findReviewByReviewId(int reviewId) {
        Review foundReview = reviewRepository.findById(reviewId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID를 가진 리뷰가 존재하지 않습니다."));

        return ReviewResponseDTO.from(foundReview);
    }

    /* 2. 리뷰 최신순 상세 조회(유저, 주문 상품별) */
    public Page<ReviewResponseDTO> findReviewByUserIdSortedUpdatedAt(int userId, int curPage) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신순 정렬)
        int page = curPage <= 0 ? 0 : curPage - 1;
        int size = 10;   // 리뷰는 한 페이지에 10개씩만
        String sortDir = "updatedAt";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 리포지토리 호출 (유저 ID로 필터링 + 페이징/정렬 적용)
        Page<Review> reviewList = reviewRepository.findAllByUser_UserId(userId, pageRequest);

        // Entity -> DTO 변환 후 반환
        return reviewList.map(ReviewResponseDTO::from);
    }

    public ReviewResponseDTO findReviewByOrderItemId(int orderItemId) {

        if (!orderItemRepository.existsById(orderItemId)) {
            throw new IllegalArgumentException("존재하지 않는 주문 상품입니다.");
        }

        // 리포지토리 호출 (주문상품 ID로 필터링 + 페이징/정렬 적용)
        Review foundReview = reviewRepository.findByOrderItem_OrderItemId(orderItemId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 리뷰입니다."));

        // Entity -> DTO 변환 후 반환
        return ReviewResponseDTO.from(foundReview);
    }

    /* 3. 리뷰 등록 */
    @Transactional
    public ReviewResponseDTO insertReview(int writerId, ReviewRequestDTO requestDTO) {
        User user = userRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        OrderItem orderItem = orderItemRepository.findById(requestDTO.getOrderItemId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

//        reviewRepository.findByOrderItem_OrderItemId(orderItem.getOrderItemId())
//                .ifPresent(review -> {
//                    // 이미 존재한다면 예외를 던집니다.
//                    throw new IllegalArgumentException("이미 해당 주문 상품에 대한 리뷰를 작성했습니다.");
//                });

        Order order = orderItem.getOrder();
        if (order == null || order.getUser().getUserId() != writerId) {
            throw new IllegalArgumentException("본인이 주문한 상품에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        // DTO -> Entity 변환 (builder 패턴 사용)
        Review newReview = Review.builder()
                .reviewContent(requestDTO.getReviewContent())
                .reviewRating(requestDTO.getReviewRating())
                .user(user)
                .orderItem(orderItem)
                .build();

//        // 내부적으로 EntityManager.persist() 호출되어 영속성 컨텍스트로 들어간다.
//        Review savedReview = reviewRepository.save(newReview);

        Review savedReview;
        try {
            savedReview = reviewRepository.save(newReview);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("이미 해당 주문 상품에 대한 리뷰를 작성했습니다.");
        }

        // 저장 후, 생성된 Entity를 다시 DTO로 변환하여 반환
        return ReviewResponseDTO.from(savedReview);
    }

    /* 4. 리뷰 수정 */
    @Transactional
    public ReviewResponseDTO modifyReviewContent(int reviewId, int currentUserId, ReviewModifyRequestDTO requestDTO) {

        Review foundReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 리뷰가 존재하지 않습니다."));

        if (foundReview.getUser().getUserId() != currentUserId) {
            throw new IllegalArgumentException("수정 권한이 없습니다. (작성자 불일치)");
        }

        foundReview.modify(
            requestDTO.getReviewContent(),
            requestDTO.getReviewRating()
        );

        return ReviewResponseDTO.from(foundReview);
    }

    /* 5. 리뷰 삭제 */
    @Transactional
    public void deleteReview(int reviewId, int userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 리뷰가 존재하지 않습니다."));

        if (review.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("삭제 권한이 없습니다. (작성자 불일치)");
        }

        reviewRepository.delete(review);
    }
    
    // 관리자용 삭제 메소드 (ID만으로 삭제)
    @Transactional
    public void deleteReview(int reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("삭제할 리뷰가 존재하지 않습니다.");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Transactional
    public void deleteAllReview() {
        reviewRepository.deleteAll();

        reviewRepository.flush();

        reviewRepository.resetAutoIncrement();
    }
}
