package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Inquiry.InquiryModifyRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
import com.sesac.fmmall.Entity.Inquiry;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.InquiryRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    /* 1. 문의 코드로 상세 조회 */
    public InquiryResponseDTO findInquiryByInquiryId(int inquiryId) {
        Inquiry foundInquiry = inquiryRepository.findById(inquiryId).orElseThrow(
                () -> new IllegalArgumentException("해당 ID를 가진 문의가 존재하지 않습니다."));

        return InquiryResponseDTO.from(foundInquiry);
    }

    /* 2. 문의 최신순 상세 조회(유저, 상품별) */
    public Page<InquiryResponseDTO> findInquiryByUserIdSortedUpdatedAt(int userId, int curPage) {

        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }

        // 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신순 정렬)
        int page = curPage <= 0 ? 0 : curPage - 1;
        int size = 10;   // 문의는 한 페이지에 10개씩만
        String sortDir = "updatedAt";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 리포지토리 호출 (유저 ID로 필터링 + 페이징/정렬 적용)
        Page<Inquiry> inquiryList = inquiryRepository.findAllByUser_UserId(userId, pageRequest);

        // Entity -> DTO 변환 후 반환
        return inquiryList.map(InquiryResponseDTO::from);
    }

    public Page<InquiryResponseDTO> findInquiryByProductIdSortedUpdatedAt(int productId, int curPage) {

        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        // 페이징 및 정렬 설정 (기존 로직과 동일: 0페이지 보정 + 최신순 정렬)
        int page = curPage <= 0 ? 0 : curPage - 1;
        int size = 10;   // 문의는 한 페이지에 10개씩만
        String sortDir = "updatedAt";

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir).descending());

        // 리포지토리 호출 (상품 ID로 필터링 + 페이징/정렬 적용)
        Page<Inquiry> inquiryList = inquiryRepository.findAllByProduct_ProductId(productId, pageRequest);

        // Entity -> DTO 변환 후 반환
        return inquiryList.map(InquiryResponseDTO::from);
    }


    /* 3. 문의 등록 */
    @Transactional
    public InquiryResponseDTO insertInquiry(int writerId, InquiryRequestDTO requestDTO) {
        User user = userRepository.findById(writerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // DTO -> Entity 변환 (builder 패턴 사용)
        Inquiry newInquiry = Inquiry.builder()
                .inquiryContent(requestDTO.getInquiryContent())
                .user(user)
                .product(product)
                .build();

        // 내부적으로 EntityManager.persist() 호출되어 영속성 컨텍스트로 들어간다.
        Inquiry savedInquiry = inquiryRepository.save(newInquiry);

        // 저장 후, 생성된 Entity를 다시 DTO로 변환하여 반환
        return InquiryResponseDTO.from(savedInquiry);
    }

    /* 4. 문의 수정 */
    @Transactional
    public InquiryResponseDTO modifyInquiryContent(int inquiryId, int currentUserId, InquiryModifyRequestDTO requestDTO) {

        Inquiry foundInquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 문의가 존재하지 않습니다."));

        if (foundInquiry.getUser().getUserId() != currentUserId) {
            throw new IllegalArgumentException("수정 권한이 없습니다. (작성자 불일치)");
        }

        foundInquiry.modifyContent(
            requestDTO.getInquiryContent()
        );

        return InquiryResponseDTO.from(foundInquiry);
    }

    /* 5. 문의 삭제 */
    @Transactional
    public void deleteInquiry(int inquiryId, int userId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("삭제할 문의가 존재하지 않습니다."));

        if (inquiry.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("삭제 권한이 없습니다. (작성자 불일치)");
        }

        inquiryRepository.delete(inquiry);
    }

    // 관리자용 삭제 메소드 (ID만으로 삭제)
    @Transactional
    public void deleteInquiry(int inquiryId) {
        if (!inquiryRepository.existsById(inquiryId)) {
            throw new IllegalArgumentException("삭제할 문의가 존재하지 않습니다.");
        }
        inquiryRepository.deleteById(inquiryId);
    }

    @Transactional
    public void deleteAllInquiry() {
        inquiryRepository.deleteAll();

        inquiryRepository.flush();

        inquiryRepository.resetAutoIncrement();
    }
}
