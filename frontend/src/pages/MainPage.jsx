import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import ProductCard from '../components/ProductCard';
import IntroStack from '../components/IntroStack';
import { productAPI, wishlistAPI } from '../services/api';
import { shouldShowIntro, markIntroAsViewed } from '../utils/introUtils';

const MainPage = () => {
    const [products, setProducts] = useState([]);
    const [filteredProducts, setFilteredProducts] = useState([]);
    const [sortOption, setSortOption] = useState('recommended');
    const [loading, setLoading] = useState(true);
    const [showIntro, setShowIntro] = useState(true);
    const [introComplete, setIntroComplete] = useState(false);

    // ✅ 위시리스트 상태 추가
    const [wishlistedProductIds, setWishlistedProductIds] = useState([]);

    // 페이지네이션 상태
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 6;

    useEffect(() => {
        if (!shouldShowIntro()) {
            setShowIntro(false);
            setIntroComplete(true);
        }

        loadProducts();
        loadWishlist(); // ✅ 위시리스트 로드 추가
    }, []);

    // 필터나 정렬이 변경되면 첫 페이지로 이동
    useEffect(() => {
        setCurrentPage(1);
    }, [filteredProducts.length]);

    const loadProducts = async () => {
        try {
            setLoading(true);
            const response = await productAPI.getAllProducts();
            const productData = response.data || [];
            setProducts(productData);
            setFilteredProducts(productData);
        } catch (error) {
            console.error('상품 로딩 실패:', error);
        } finally {
            setLoading(false);
            setTimeout(() => {
                handleIntroComplete();
            }, 7000);
        }
    };

    // ✅ 위시리스트 로드 함수 추가
    const loadWishlist = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            // 로그인하지 않은 경우 빈 배열
            setWishlistedProductIds([]);
            return;
        }

        try {
            // 전체 위시리스트를 가져오기 (페이지네이션 무시)
            const response = await wishlistAPI.getMyWishlists(1);

            // 위시리스트의 모든 페이지 데이터를 가져오기
            const totalPages = response.data.totalPages || 1;
            let allWishlistItems = response.data.content || [];

            // 나머지 페이지들도 가져오기
            for (let page = 2; page <= totalPages; page++) {
                const nextResponse = await wishlistAPI.getMyWishlists(page);
                allWishlistItems = [...allWishlistItems, ...(nextResponse.data.content || [])];
            }

            // productId만 추출하여 배열로 저장
            const productIds = allWishlistItems.map(item => item.productId);
            setWishlistedProductIds(productIds);
        } catch (error) {
            console.error('위시리스트 로딩 실패:', error);
            // 에러 발생 시 빈 배열로 설정
            setWishlistedProductIds([]);
        }
    };

    const handleIntroComplete = () => {
        markIntroAsViewed();
        setShowIntro(false);
        setTimeout(() => setIntroComplete(true), 500);
    };

    const handleFilterChange = (filters) => {
        let filtered = [...products];

        // 1) 카테고리
        if (filters.categoryId !== null && filters.categoryId !== undefined) {
            filtered = filtered.filter(p => p.categoryId === filters.categoryId);
        }

        // 2) 브랜드
        if (filters.brandIds && filters.brandIds.length > 0) {
            filtered = filtered.filter(p =>
                filters.brandIds.includes(p.brandId)
            );
        }

        // 3) 가격대
        if (filters.priceRange) {
            const min =
                filters.priceRange.min !== ''
                    ? Number(filters.priceRange.min)
                    : null;
            const max =
                filters.priceRange.max !== ''
                    ? Number(filters.priceRange.max)
                    : null;

            if (min !== null && !Number.isNaN(min)) {
                filtered = filtered.filter(p => p.productPrice >= min);
            }

            if (max !== null && !Number.isNaN(max)) {
                filtered = filtered.filter(p => p.productPrice <= max);
            }
        }

        setFilteredProducts(filtered);
    };

    const handleSortChange = (sortValue) => {
        setSortOption(sortValue);
        let sorted = [...filteredProducts];

        switch (sortValue) {
            case 'price-low':
                sorted.sort((a, b) => a.productPrice - b.productPrice);
                break;
            case 'price-high':
                sorted.sort((a, b) => b.productPrice - a.productPrice);
                break;
            case 'newest':
                sorted.sort((a, b) => b.productId - a.productId);
                break;
            default:
                break;
        }

        setFilteredProducts(sorted);
    };

    // 페이지네이션 계산
    const totalPages = Math.ceil(filteredProducts.length / itemsPerPage);
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentProducts = filteredProducts.slice(indexOfFirstItem, indexOfLastItem);

    // 페이지 변경 핸들러
    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    // 페이지 번호 배열 생성 (최대 5개 표시)
    const getPageNumbers = () => {
        const pageNumbers = [];
        const maxPagesToShow = 5;

        let startPage = Math.max(1, currentPage - Math.floor(maxPagesToShow / 2));
        let endPage = Math.min(totalPages, startPage + maxPagesToShow - 1);

        if (endPage - startPage < maxPagesToShow - 1) {
            startPage = Math.max(1, endPage - maxPagesToShow + 1);
        }

        for (let i = startPage; i <= endPage; i++) {
            pageNumbers.push(i);
        }

        return pageNumbers;
    };

    // 인트로 화면
    if (!introComplete) {
        return (
            <div style={{
                opacity: showIntro ? 1 : 0,
                transition: 'opacity 0.5s ease-out'
            }}>
                <IntroStack onComplete={handleIntroComplete} />
            </div>
        );
    }

    // 메인 콘텐츠
    return (
        <main className="main">
            <Sidebar onFilterChange={handleFilterChange} />

            <section className="content">
                <div className="toolbar">
                    <div className="toolbar__left">
                        <h1 className="toolbar__title">전체 상품</h1>
                        <p className="toolbar__subtitle">{filteredProducts.length}개의 상품</p>
                    </div>

                    <select
                        className="select"
                        value={sortOption}
                        onChange={(e) => handleSortChange(e.target.value)}
                    >
                        <option value="recommended">추천순</option>
                        <option value="newest">신상품순</option>
                        <option value="price-low">낮은 가격순</option>
                        <option value="price-high">높은 가격순</option>
                    </select>
                </div>

                <section className="product-grid">
                    {loading ? (
                        <div style={{
                            gridColumn: '1 / -1',
                            textAlign: 'center',
                            padding: '3rem',
                            color: '#6b7280'
                        }}>
                            <p>상품을 불러오는 중...</p>
                        </div>
                    ) : currentProducts.length > 0 ? (
                        currentProducts.map((product) => (
                            <ProductCard
                                key={product.productId}
                                product={product}
                                wishlistedProductIds={wishlistedProductIds} // ✅ 위시리스트 전달
                            />
                        ))
                    ) : (
                        <div style={{
                            gridColumn: '1 / -1',
                            textAlign: 'center',
                            padding: '3rem',
                            color: '#6b7280'
                        }}>
                            <p>해당 조건의 상품이 없습니다.</p>
                        </div>
                    )}
                </section>

                {/* 페이지네이션 */}
                {!loading && filteredProducts.length > 0 && totalPages > 1 && (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        gap: '0.5rem',
                        marginTop: '2rem',
                        paddingBottom: '1rem'
                    }}>
                        {/* 이전 버튼 */}
                        <button
                            onClick={() => handlePageChange(currentPage - 1)}
                            disabled={currentPage === 1}
                            className="btn btn--outline"
                            style={{
                                padding: '0.5rem 1rem',
                                opacity: currentPage === 1 ? 0.5 : 1,
                                cursor: currentPage === 1 ? 'not-allowed' : 'pointer'
                            }}
                        >
                            이전
                        </button>

                        {/* 페이지 번호들 */}
                        {getPageNumbers().map((pageNum) => (
                            <button
                                key={pageNum}
                                onClick={() => handlePageChange(pageNum)}
                                className="btn"
                                style={{
                                    padding: '0.5rem 1rem',
                                    backgroundColor: currentPage === pageNum ? '#111827' : 'transparent',
                                    color: currentPage === pageNum ? '#ffffff' : '#4b5563',
                                    border: '1px solid',
                                    borderColor: currentPage === pageNum ? '#111827' : 'rgba(209, 213, 219, 0.8)',
                                    cursor: 'pointer',
                                    minWidth: '2.5rem'
                                }}
                            >
                                {pageNum}
                            </button>
                        ))}

                        {/* 다음 버튼 */}
                        <button
                            onClick={() => handlePageChange(currentPage + 1)}
                            disabled={currentPage === totalPages}
                            className="btn btn--outline"
                            style={{
                                padding: '0.5rem 1rem',
                                opacity: currentPage === totalPages ? 0.5 : 1,
                                cursor: currentPage === totalPages ? 'not-allowed' : 'pointer'
                            }}
                        >
                            다음
                        </button>
                    </div>
                )}
            </section>
        </main>
    );
};

export default MainPage;