package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.WishList.WishListRequestDTO;
import com.sesac.fmmall.DTO.WishList.WishListResponseDTO;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.WishList;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.WishListRepository;
import com.sesac.fmmall.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class WishListService {
    private final WishListRepository wishListRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /* 1. 위시리스트 코드로 상세 조회 */
    public WishListResponseDTO findWishListByWishListId(int wishListId, int userId) {
        WishList foundWishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID를 가진 위시리스트가 존재하지 않습니다."));

        if (foundWishList.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("조회 권한이 없습니다.");
        }

        return WishListResponseDTO.from(foundWishList);
    }

    /* 2. 위시리스트 전체 조회 (관리자용) */
    public List<WishListResponseDTO> findAllWishList() {
        List<WishList> foundWishListList = wishListRepository.findAll();

        return foundWishListList.stream().map(WishListResponseDTO::from)
                .collect(Collectors.toList());
    }

    /* 3. 유저별 위시리스트 생성순 상세 조회 */
    public Page<WishListResponseDTO> findWishListByUserIdSortedCreatedAt(int userId, int curPage) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신 생성순 정렬)
        int page = curPage <= 0 ? 0 : curPage - 1;
        int size = 20;   // 위시리스트는 한 페이지에 20개씩만
        String sortDir = "createdAt";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 리포지토리 호출 (유저 ID로 필터링 + 페이징/정렬 적용)
        Page<WishList> wishListList = wishListRepository.findAllByUser_UserId(userId, pageRequest);

        // Entity -> DTO 변환 후 반환
        return wishListList.map(WishListResponseDTO::from);
    }

    /* 4. 위시리스트 삭제 */
    @Transactional
    public void deleteWishList(int wishListId, int userId) {
        WishList wishList = wishListRepository.findById(wishListId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 위시리스트가 존재하지 않습니다."));

        if (wishList.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        wishListRepository.delete(wishList);
    }

    /* 5. 위시리스트 토글 형식. 사실상 삽입, 삭제를 담당하기에 위에 것들은 필요없음. */
    @Transactional
    public WishListResponseDTO toggleWishlist(int currentUserId, WishListRequestDTO requestDTO) {
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        int deletedCount = wishListRepository.deleteByUser_UserIdAndProduct_ProductId(
                currentUserId, requestDTO.getProductId()
        );

        if (deletedCount > 0) {
            return WishListResponseDTO.removedDTO(); // 삭제 성공 응답
        } else {
            // 삭제 실패 (Count=0): INSERT 시도
            WishList newWishList = WishList.builder()
                    .user(user)
                    .product(product)
                    .build();

            try {
                // 동시성 위험을 DB 제약 조건으로 해결합니다.
                WishList savedWishList = wishListRepository.save(newWishList);
                return WishListResponseDTO.from(savedWishList); // 추가 성공 응답
            } catch (DataIntegrityViolationException ex) {
                throw new IllegalArgumentException("이미 위시리스트에 추가된 상품입니다.");
            }
        }
//        Optional<Integer> wishListItem =
//                wishListRepository.findIdByUserIdAndProductId(
//                        currentUserId, requestDTO.getProductId()
//                );

//        if (wishListItem.isPresent()) {
//            wishListRepository.deleteById(wishListItem.get());
//            return WishListResponseDTO.removedDTO(); // 삭제
//        } else {
//            WishList newWishList = WishList.builder()
//                    .user(user)
//                    .product(product)
//                    .build();
//            WishList savedWishList = wishListRepository.save(newWishList);
//            return WishListResponseDTO.from(savedWishList); // 추가
//        }
    }

    @Transactional
    public void deleteAllWishList() {
        wishListRepository.deleteAll();

        wishListRepository.flush();

        wishListRepository.resetAutoIncrement();
    }
}
