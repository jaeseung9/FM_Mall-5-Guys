import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartAPI, wishlistAPI } from '../services/api';

const ProductCard = ({ product, wishlistedProductIds = [] }) => {
    const navigate = useNavigate();
    const [isWishlisted, setIsWishlisted] = useState(false);
    const [isProcessing, setIsProcessing] = useState(false);

    // ✅ 컴포넌트가 마운트될 때 위시리스트 상태 확인
    useEffect(() => {
        // wishlistedProductIds 배열에 현재 제품 ID가 있는지 확인
        setIsWishlisted(wishlistedProductIds.includes(product.productId));
    }, [wishlistedProductIds, product.productId]);

    const handleCardClick = () => {
        navigate(`/product/${product.productId}`);
    };

    const handleAddToCart = async (e) => {
        e.stopPropagation();

        // 로그인 확인
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인이 필요합니다.');
            navigate('/login');
            return;
        }

        try {
            // 장바구니에 상품 추가 API 호출
            await cartAPI.addToCart({
                productId: product.productId,
                quantity: 1
            });

            alert('장바구니에 추가되었습니다.');

            // 장바구니로 이동할지 물어보기
            if (window.confirm('장바구니로 이동하시겠습니까?')) {
                navigate('/cart');
            }
        } catch (error) {
            console.error('장바구니 추가 실패:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            } else if (error.response?.status === 404) {
                alert('상품을 찾을 수 없습니다.');
            } else {
                alert('장바구니 추가에 실패했습니다.');
            }
        }
    };

    const handleWishlistToggle = async (e) => {
        e.stopPropagation();

        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인이 필요한 서비스입니다.');
            navigate('/login');
            return;
        }

        if (isProcessing) return;

        try {
            setIsProcessing(true);
            const response = await wishlistAPI.toggleWishlist({
                productId: product.productId
            });

            const isAdded = response.data.isAdded ?? response.data.added;
            setIsWishlisted(isAdded);

            if (isAdded) {
                alert('위시리스트에 추가되었습니다.');
            } else {
                alert('위시리스트에서 제거되었습니다.');
            }
        } catch (error) {
            console.error('위시리스트 토글 실패:', error);
            if (error.response?.status === 401) {
                alert('로그인이 만료되었습니다. 다시 로그인해주세요.');
                localStorage.removeItem('token');
                navigate('/login');
            } else {
                alert('위시리스트 처리 중 오류가 발생했습니다.');
            }
        } finally {
            setIsProcessing(false);
        }
    };

    return (
        <article className="product-card" onClick={handleCardClick} style={{ cursor: 'pointer', position: 'relative' }}>
            {product.isNew && (
                <div className="product-card__badge">NEW</div>
            )}
            {product.isHot && (
                <div className="product-card__badge product-card__badge--green">HOT</div>
            )}

            {/* ✅ 위시리스트 버튼 */}
            <button
                className={`product-card__wishlist ${isWishlisted ? 'product-card__wishlist--active' : ''}`}
                onClick={handleWishlistToggle}
                disabled={isProcessing}
                title={isWishlisted ? '위시리스트에서 제거' : '위시리스트에 추가'}
                style={{
                    position: 'absolute',
                    top: '1rem',
                    right: '1rem',
                    width: '2.5rem',
                    height: '2.5rem',
                    borderRadius: '50%',
                    border: 'none',
                    backgroundColor: 'rgba(255, 255, 255, 0.9)',
                    cursor: 'pointer',
                    fontSize: '1.3rem',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                    transition: 'all 0.2s',
                    zIndex: 10
                }}
            >
                {isWishlisted ? '💝' : '🤍'}
            </button>

            <div className="product-card__image">
                {product.imageUrl ? (
                    <img
                        src={product.imageUrl}
                        alt={product.productName}
                        style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: '0.75rem' }}
                    />
                ) : (
                    <span>{product.categoryName || '상품'}</span>
                )}
            </div>

            <h3 className="product-card__name">{product.productName}</h3>
            <p className="product-card__brand">{product.brandName || '브랜드'}</p>

            <p className="product-card__price">
                {product.productPrice?.toLocaleString('ko-KR')}원
            </p>

            <p className="product-card__desc">
                {product.description || '상품 설명이 없습니다.'}
            </p>

            <button
                className="btn btn--outline full-width"
                onClick={handleAddToCart}
            >
                장바구니 담기
            </button>
        </article>
    );
};

export default ProductCard;