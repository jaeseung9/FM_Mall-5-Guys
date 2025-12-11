import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartAPI } from '../services/api';

const CartPage = () => {
    const [cartData, setCartData] = useState(null);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        loadCart();
    }, []);

    const loadCart = async () => {
        try {
            const response = await cartAPI.getCart();
            setCartData(response.data);
        } catch (error) {
            console.error('장바구니 로딩 실패:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleQuantityChange = async (cartItemId, newQuantity) => {
        if (newQuantity < 1) {
            alert('수량은 1개 이상이어야 합니다.');
            return;
        }

        try {
            await cartAPI.updateCartItem(cartItemId, { quantity: newQuantity });
            loadCart();
        } catch (error) {
            console.error('수량 변경 실패:', error);
            alert('수량 변경에 실패했습니다.');
        }
    };

    const handleRemoveItem = async (cartItemId) => {
        if (!window.confirm('이 상품을 장바구니에서 삭제하시겠습니까?')) return;

        try {
            await cartAPI.removeCartItem(cartItemId);
            loadCart();
        } catch (error) {
            console.error('상품 삭제 실패:', error);
            alert('상품 삭제에 실패했습니다.');
        }
    };

    const handleClearCart = async () => {
        if (!window.confirm('장바구니를 모두 비우시겠습니까?')) return;

        try {
            await cartAPI.clearCart();
            loadCart();
        } catch (error) {
            console.error('장바구니 비우기 실패:', error);
            alert('장바구니 비우기에 실패했습니다.');
        }
    };

    const handleCheckout = () => {
        if (!cartData || !cartData.itemList || cartData.itemList.length === 0) {
            alert('장바구니가 비어있습니다.');
            return;
        }
        // 👉 결제 페이지로 이동
        navigate('/cart/checkout');
    };

    if (loading) {
        return (
            <main className="main" style={{ textAlign: 'center', padding: '3rem' }}>
                <p>로딩 중...</p>
            </main>
        );
    }

    return (
        <main className="main" style={{ gridTemplateColumns: '1fr', maxWidth: '1200px', margin: '0 auto' }}>
            <div style={{ backgroundColor: '#ffffff', borderRadius: '1rem', padding: '2rem' }}>
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-between',
                    alignItems: 'center',
                    marginBottom: '2rem',
                    borderBottom: '2px solid #111827',
                    paddingBottom: '1rem'
                }}>
                    <h1 style={{ fontSize: '1.75rem', fontWeight: '700' }}>장바구니</h1>
                    {cartData && cartData.itemList && cartData.itemList.length > 0 && (
                        <button
                            onClick={handleClearCart}
                            className="btn btn--ghost"
                            style={{ fontSize: '0.9rem' }}
                        >
                            전체 삭제
                        </button>
                    )}
                </div>

                {!cartData || !cartData.itemList || cartData.itemList.length === 0 ? (
                    <div style={{
                        textAlign: 'center',
                        padding: '4rem 2rem',
                        color: '#6b7280'
                    }}>
                        <p style={{ fontSize: '1.1rem', marginBottom: '1.5rem' }}>
                            장바구니가 비어있습니다.
                        </p>
                        <button
                            onClick={() => navigate('/')}
                            className="btn btn--primary"
                        >
                            쇼핑 계속하기
                        </button>
                    </div>
                ) : (
                    <>
                        <div style={{ marginBottom: '2rem' }}>
                            {cartData.itemList.map((item) => (
                                <div
                                    key={item.cartItemId}
                                    style={{
                                        display: 'flex',
                                        gap: '1.5rem',
                                        padding: '1.5rem',
                                        border: '1px solid #e5e7eb',
                                        borderRadius: '0.5rem',
                                        marginBottom: '1rem',
                                        alignItems: 'center'
                                    }}
                                >
                                    {/* 상품 이미지 영역 */}
                                    <div style={{
                                        width: '120px',
                                        height: '120px',
                                        backgroundColor: '#f3f4f6',
                                        borderRadius: '0.5rem',
                                        display: 'flex',
                                        alignItems: 'center',
                                        justifyContent: 'center',
                                        flexShrink: 0
                                    }}>
                                        {item.productImage ? (
                                            <img
                                                src={item.productImage}
                                                alt={item.productName}
                                                style={{
                                                    maxWidth: '100%',
                                                    maxHeight: '100%',
                                                    objectFit: 'contain'
                                                }}
                                            />
                                        ) : (
                                            <span style={{ color: '#9ca3af' }}>이미지 없음</span>
                                        )}
                                    </div>

                                    {/* 상품 정보 */}
                                    <div style={{ flex: 1 }}>
                                        <h3 style={{
                                            fontSize: '1.1rem',
                                            fontWeight: '600',
                                            marginBottom: '0.5rem'
                                        }}>
                                            {item.productName}
                                        </h3>
                                        <p style={{
                                            fontSize: '1.25rem',
                                            fontWeight: '700',
                                            color: '#111827',
                                            marginBottom: '1rem'
                                        }}>
                                            {item.productPrice.toLocaleString()}원
                                        </p>
                                        <div style={{
                                            display: 'flex',
                                            alignItems: 'center',
                                            gap: '0.5rem'
                                        }}>
                                            <button
                                                onClick={() => handleQuantityChange(item.cartItemId, item.cartItemQuantity - 1)}
                                                className="btn btn--ghost"
                                                style={{
                                                    padding: '0.25rem 0.75rem',
                                                    fontSize: '1rem'
                                                }}
                                            >
                                                -
                                            </button>
                                            <span style={{
                                                padding: '0.25rem 1rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.25rem',
                                                minWidth: '3rem',
                                                textAlign: 'center'
                                            }}>
                                                {item.cartItemQuantity}
                                            </span>
                                            <button
                                                onClick={() => handleQuantityChange(item.cartItemId, item.cartItemQuantity + 1)}
                                                className="btn btn--ghost"
                                                style={{
                                                    padding: '0.25rem 0.75rem',
                                                    fontSize: '1rem'
                                                }}
                                            >
                                                +
                                            </button>
                                        </div>
                                    </div>

                                    {/* 가격 및 삭제 버튼 */}
                                    <div style={{
                                        textAlign: 'right',
                                        display: 'flex',
                                        flexDirection: 'column',
                                        alignItems: 'flex-end',
                                        gap: '1rem'
                                    }}>
                                        <p style={{
                                            fontSize: '1.5rem',
                                            fontWeight: '700',
                                            color: '#111827'
                                        }}>
                                            {item.totalPrice.toLocaleString()}원
                                        </p>
                                        <button
                                            onClick={() => handleRemoveItem(item.cartItemId)}
                                            className="btn btn--ghost"
                                            style={{
                                                fontSize: '0.9rem',
                                                color: '#ef4444'
                                            }}
                                        >
                                            삭제
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>

                        {/* 주문 요약 */}
                        <div style={{
                            backgroundColor: '#f9fafb',
                            padding: '2rem',
                            borderRadius: '0.5rem',
                            marginBottom: '2rem'
                        }}>
                            <div style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                marginBottom: '1rem',
                                fontSize: '1rem'
                            }}>
                                <span>총 상품 개수</span>
                                <span style={{ fontWeight: '600' }}>
                                    {cartData.totalItemCount}개
                                </span>
                            </div>
                            <div style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                paddingTop: '1rem',
                                borderTop: '1px solid #e5e7eb',
                                fontSize: '1.25rem',
                                fontWeight: '700'
                            }}>
                                <span>총 결제 금액</span>
                                <span style={{ color: '#111827' }}>
                                    {cartData.totalPrice.toLocaleString()}원
                                </span>
                            </div>
                        </div>

                        {/* 액션 버튼 */}
                        <div style={{
                            display: 'flex',
                            gap: '1rem',
                            justifyContent: 'flex-end'
                        }}>
                            <button
                                onClick={() => navigate('/')}
                                className="btn btn--ghost"
                                style={{ padding: '1rem 2rem' }}
                            >
                                쇼핑 계속하기
                            </button>
                            <button
                                onClick={handleCheckout}
                                className="btn btn--primary"
                                style={{ padding: '1rem 2rem' }}
                            >
                                주문하기
                            </button>
                        </div>
                    </>
                )}
            </div>
        </main>
    );
};

export default CartPage;