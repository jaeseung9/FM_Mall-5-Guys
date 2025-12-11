import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { wishlistAPI, cartAPI } from '../services/api';

const WishListPage = () => {
    const [wishlistItems, setWishlistItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const navigate = useNavigate();

    useEffect(() => {
        loadWishlist();
    }, [currentPage]);

    const loadWishlist = async () => {
        try {
            setLoading(true);
            const response = await wishlistAPI.getMyWishlists(currentPage);

            // Page 객체 형식으로 받아옴
            setWishlistItems(response.data.content || []);
            setTotalPages(response.data.totalPages || 1);
        } catch (error) {
            console.error('위시리스트 로딩 실패:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleRemoveWishlist = async (wishListId) => {
        if (!window.confirm('위시리스트에서 삭제하시겠습니까?')) {
            return;
        }

        try {
            await wishlistAPI.deleteWishlist(wishListId);
            alert('위시리스트에서 삭제되었습니다.');
            loadWishlist(); // 목록 새로고침
        } catch (error) {
            console.error('위시리스트 삭제 실패:', error);
            alert('삭제에 실패했습니다.');
        }
    };

    const handleAddToCart = async (productId, wishListId) => {
        try {
            await cartAPI.addToCart({
                productId: productId,
                quantity: 1
            });

            if (window.confirm('장바구니에 추가되었습니다. 위시리스트에서 삭제하시겠습니까?')) {
                await wishlistAPI.deleteWishlist(wishListId);
                loadWishlist();
            }
        } catch (error) {
            console.error('장바구니 추가 실패:', error);
            alert('장바구니 추가에 실패했습니다.');
        }
    };

    const handleProductClick = (productId) => {
        navigate(`/product/${productId}`);
    };

    if (loading) {
        return (
            <main className="container" style={{ paddingTop: '2rem' }}>
                <div style={{ textAlign: 'center', padding: '3rem 0' }}>
                    <p>로딩 중...</p>
                </div>
            </main>
        );
    }

    return (
        <main className="container" style={{ paddingTop: '2rem', paddingBottom: '3rem' }}>
            <section className="page-section">
                {/* 헤더 */}
                <div className="section-header" style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginBottom: '2rem'
                }}>
                    <div>
                        <h1 style={{ fontSize: '1.75rem', fontWeight: '700', marginBottom: '0.5rem' }}>
                            내 위시리스트
                        </h1>
                        <p style={{ color: '#6b7280', fontSize: '0.95rem' }}>
                            찜한 상품 {wishlistItems.length}개
                        </p>
                    </div>
                    <button
                        onClick={() => navigate('/mypage')}
                        className="btn btn--ghost"
                    >
                        ← 마이페이지
                    </button>
                </div>

                {/* 위시리스트 목록 */}
                {wishlistItems.length === 0 ? (
                    <div style={{
                        textAlign: 'center',
                        padding: '4rem 2rem',
                        backgroundColor: '#f9fafb',
                        borderRadius: '0.75rem'
                    }}>
                        <p style={{ fontSize: '1.1rem', color: '#6b7280', marginBottom: '1rem' }}>
                            위시리스트가 비어있습니다.
                        </p>
                        <button
                            onClick={() => navigate('/')}
                            className="btn btn--primary"
                        >
                            상품 둘러보기
                        </button>
                    </div>
                ) : (
                    <div style={{
                        display: 'grid',
                        gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
                        gap: '1.5rem'
                    }}>
                        {wishlistItems.map((item) => (
                            <div
                                key={item.wishListId}
                                style={{
                                    backgroundColor: '#ffffff',
                                    border: '1px solid #e5e7eb',
                                    borderRadius: '0.75rem',
                                    padding: '1rem',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: '0.75rem'
                                }}
                            >
                                {/* 상품 이미지 영역 */}
                                <div
                                    onClick={() => handleProductClick(item.productId)}
                                    style={{
                                        width: '100%',
                                        height: '200px',
                                        backgroundColor: '#f3f4f6',
                                        borderRadius: '0.5rem',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        cursor: 'pointer',
                                        position: 'relative'
                                    }}
                                >
                                    <span style={{ color: '#9ca3af', fontSize: '0.9rem' }}>
                                        상품 ID: {item.productId}
                                    </span>
                                    <div style={{
                                        position: 'absolute',
                                        top: '0.5rem',
                                        right: '0.5rem',
                                        fontSize: '0.75rem',
                                        color: '#6b7280'
                                    }}>
                                        {item.createdAt && new Date(item.createdAt).toLocaleDateString('ko-KR')}
                                    </div>
                                </div>

                                {/* 버튼 그룹 */}
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                                    <button
                                        onClick={() => handleAddToCart(item.productId, item.wishListId)}
                                        className="btn btn--primary"
                                        style={{ width: '100%' }}
                                    >
                                        장바구니 담기
                                    </button>
                                    <button
                                        onClick={() => handleRemoveWishlist(item.wishListId)}
                                        className="btn btn--outline"
                                        style={{ width: '100%', color: '#dc2626', borderColor: '#dc2626' }}
                                    >
                                        삭제
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                )}

                {/* 페이지네이션 */}
                {totalPages > 1 && (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        gap: '0.5rem',
                        marginTop: '2rem'
                    }}>
                        <button
                            onClick={() => setCurrentPage(prev => Math.max(1, prev - 1))}
                            disabled={currentPage === 1}
                            className="btn btn--outline"
                            style={{ padding: '0.5rem 1rem' }}
                        >
                            이전
                        </button>
                        <span style={{ padding: '0.5rem 1rem', display: 'flex', alignItems: 'center' }}>
                            {currentPage} / {totalPages}
                        </span>
                        <button
                            onClick={() => setCurrentPage(prev => Math.min(totalPages, prev + 1))}
                            disabled={currentPage === totalPages}
                            className="btn btn--outline"
                            style={{ padding: '0.5rem 1rem' }}
                        >
                            다음
                        </button>
                    </div>
                )}
            </section>
        </main>
    );
};

export default WishListPage;