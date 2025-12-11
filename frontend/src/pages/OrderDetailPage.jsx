import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { orderAPI, reviewAPI } from '../services/api';

const OrderDetailPage = () => {
    const { orderId } = useParams();
    const [order, setOrder] = useState(null);
    const [loading, setLoading] = useState(true);
    const [showReviewModal, setShowReviewModal] = useState(false);
    const [selectedOrderItem, setSelectedOrderItem] = useState(null);
    const [reviewForm, setReviewForm] = useState({
        reviewRating: 5.0,
        reviewContent: '',
        orderItemId: null
    });
    const [existingReviews, setExistingReviews] = useState({});
    const navigate = useNavigate();

    useEffect(() => {
        loadOrderDetail();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [orderId]);

    const loadOrderDetail = async () => {
        try {
            const response = await orderAPI.getOrderDetail(orderId);
            setOrder(response.data);

            // ✅ 각 상품에 대한 리뷰 존재 여부 확인
            if (response.data.items) {
                await checkExistingReviews(response.data.items);
            }
        } catch (error) {
            console.error('주문 상세 조회 실패:', error);
            alert('주문 상세를 불러오는 데 실패했습니다.');
            navigate('/orders');
        } finally {
            setLoading(false);
        }
    };

    // ✅ 각 주문 상품에 대한 리뷰가 이미 있는지 확인
    const checkExistingReviews = async (items) => {
        const reviews = {};
        for (const item of items) {
            try {
                const response = await reviewAPI.getReviewByOrderItem(item.orderItemId);
                if (response.data) {
                    reviews[item.orderItemId] = response.data;
                }
            } catch (error) {
                // 404면 리뷰가 없는 것 - 정상
                if (error.response?.status !== 404) {
                    console.error('리뷰 확인 실패:', error);
                }
            }
        }
        setExistingReviews(reviews);
    };

    const handleOpenReviewModal = (item) => {
        setSelectedOrderItem(item);
        setReviewForm({
            reviewRating: 5.0,
            reviewContent: '',
            orderItemId: item.orderItemId
        });
        setShowReviewModal(true);
    };

    const handleCloseReviewModal = () => {
        setShowReviewModal(false);
        setSelectedOrderItem(null);
        setReviewForm({
            reviewRating: 5.0,
            reviewContent: '',
            orderItemId: null
        });
    };

    const handleSubmitReview = async (e) => {
        e.preventDefault();

        if (!reviewForm.reviewContent.trim()) {
            alert('리뷰 내용을 입력해주세요.');
            return;
        }

        try {
            await reviewAPI.createReview(reviewForm);
            alert('리뷰가 등록되었습니다.');
            handleCloseReviewModal();
            loadOrderDetail(); // 리뷰 상태 새로고침
        } catch (error) {
            console.error('리뷰 등록 실패:', error);
            if (error.response?.status === 400 && error.response?.data?.includes('이미')) {
                alert('이미 이 상품에 대한 리뷰를 작성했습니다.');
            } else {
                alert('리뷰 등록에 실패했습니다.');
            }
        }
    };

    const handleViewReview = (reviewId) => {
        navigate('/mypage'); // 마이페이지의 리뷰 탭으로 이동
    };

    if (loading) {
        return (
            <main className="main">
                <aside className="sidebar">
                    <div className="sidebar__section">
                        <div className="sidebar__title">주문 메뉴</div>
                        <ul className="sidebar__list">
                            <li>주문 내역</li>
                            <li>
                                <strong>주문 상세</strong>
                            </li>
                        </ul>
                    </div>
                </aside>
                <section className="content">
                    <div className="order-header">
                        <div>
                            <h1 className="order-header__title">주문 상세</h1>
                            <p className="order-header__subtitle">로딩 중...</p>
                        </div>
                    </div>
                </section>
            </main>
        );
    }

    if (!order) {
        return (
            <main className="main">
                <aside className="sidebar">
                    <div className="sidebar__section">
                        <div className="sidebar__title">주문 메뉴</div>
                        <ul className="sidebar__list">
                            <li>주문 내역</li>
                            <li>
                                <strong>주문 상세</strong>
                            </li>
                        </ul>
                    </div>
                </aside>
                <section className="content">
                    <div className="order-header">
                        <div>
                            <h1 className="order-header__title">주문 상세</h1>
                            <p className="order-header__subtitle">주문 정보를 찾을 수 없습니다.</p>
                        </div>
                    </div>
                </section>
            </main>
        );
    }

    return (
        <main className="main">
            <aside className="sidebar">
                <div className="sidebar__section">
                    <div className="sidebar__title">주문 메뉴</div>
                    <ul className="sidebar__list">
                        <li>
                            <button
                                type="button"
                                className="btn btn--ghost"
                                onClick={() => navigate('/orders')}
                            >
                                주문 내역
                            </button>
                        </li>
                        <li>
                            <strong>주문 상세</strong>
                        </li>
                    </ul>
                </div>
            </aside>

            <section className="content">
                <div className="order-header">
                    <div>
                        <h1 className="order-header__title">주문 상세</h1>
                        <p className="order-header__subtitle">
                            주문번호 #{order.orderId} /{' '}
                            {order.createdAt?.replace('T', ' ').slice(0, 16)}
                        </p>
                    </div>
                    <button
                        type="button"
                        className="btn btn--ghost"
                        onClick={() => navigate('/orders')}
                    >
                        주문 목록으로
                    </button>
                </div>

                {/* 배송지 정보 */}
                <div className="order-section">
                    <div className="order-section__header">
                        <h2 className="order-section__title">배송지 정보</h2>
                    </div>
                    <p className="text-muted">주문 시점의 배송지 정보입니다.</p>
                    <div style={{ marginTop: '0.75rem' }}>
                        <div className="order-info-row">
                            <span className="order-info-label">수령인</span>
                            {order.receiverName} ({order.receiverPhone})
                        </div>
                        <div className="order-info-row">
                            <span className="order-info-label">주소</span>
                            ({order.zipcode}) {order.address1} {order.address2}
                        </div>
                    </div>
                </div>

                {/* 주문 상품 목록 */}
                <div className="order-section">
                    <div className="order-section__header">
                        <h2 className="order-section__title">주문 상품</h2>
                    </div>

                    {order.items && order.items.length > 0 ? (
                        <table className="order-table">
                            <thead>
                            <tr>
                                <th>상품명</th>
                                <th className="text-center">수량</th>
                                <th className="text-right">상품 금액</th>
                                <th className="text-right">합계</th>
                                <th className="text-center">리뷰</th>
                            </tr>
                            </thead>
                            <tbody>
                            {order.items.map((item) => (
                                <tr key={item.orderItemId}>
                                    <td>{item.productName}</td>
                                    <td className="text-center">{item.quantity}</td>
                                    <td className="text-right">
                                        {item.productPrice?.toLocaleString()}원
                                    </td>
                                    <td className="text-right">
                                        {item.lineTotalPrice?.toLocaleString()}원
                                    </td>
                                    <td className="text-center">
                                        {/* ✅ 리뷰 버튼 */}
                                        {existingReviews[item.orderItemId] ? (
                                            <button
                                                className="btn btn--ghost"
                                                style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem' }}
                                                onClick={() => handleViewReview(existingReviews[item.orderItemId].reviewId)}
                                            >
                                                내 리뷰 보기
                                            </button>
                                        ) : (
                                            <button
                                                className="btn btn--primary"
                                                style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem' }}
                                                onClick={() => handleOpenReviewModal(item)}
                                            >
                                                ⭐ 리뷰 작성
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    ) : (
                        <p className="text-muted" style={{ marginTop: '0.75rem' }}>
                            주문 상품 정보가 없습니다.
                        </p>
                    )}
                </div>

                {/* 결제 정보 */}
                <div className="order-section">
                    <div className="order-section__header">
                        <h2 className="order-section__title">결제 정보</h2>
                    </div>
                    <p className="text-muted">
                        실제 결제 시스템 연동 전까지는 가상 결제 정보로 처리됩니다.
                    </p>

                    <div style={{ marginTop: '0.75rem' }}>
                        <div className="order-info-row">
                            <span className="order-info-label">총 결제 금액</span>
                            {order.totalPrice?.toLocaleString()}원
                        </div>

                        {order.payment && (
                            <>
                                <div className="order-info-row">
                                    <span className="order-info-label">결제수단</span>
                                    {order.payment.paymentMethodType}
                                </div>
                                {order.payment.approvedAt && (
                                    <div className="text-small text-muted">
                                        결제일시:{' '}
                                        {order.payment.approvedAt.replace('T', ' ').slice(0, 16)}
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                </div>

                {/* ✅ 리뷰 작성 모달 */}
                {showReviewModal && (
                    <div style={{
                        position: 'fixed',
                        top: 0,
                        left: 0,
                        right: 0,
                        bottom: 0,
                        backgroundColor: 'rgba(0, 0, 0, 0.5)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        zIndex: 1000
                    }}>
                        <div style={{
                            backgroundColor: '#ffffff',
                            borderRadius: '1rem',
                            padding: '2rem',
                            maxWidth: '500px',
                            width: '90%',
                            maxHeight: '80vh',
                            overflow: 'auto'
                        }}>
                            <h2 style={{ fontSize: '1.5rem', fontWeight: '700', marginBottom: '1.5rem' }}>
                                리뷰 작성
                            </h2>

                            {selectedOrderItem && (
                                <div style={{
                                    marginBottom: '1.5rem',
                                    padding: '1rem',
                                    backgroundColor: '#f9fafb',
                                    borderRadius: '0.5rem'
                                }}>
                                    <div style={{ fontWeight: '600', marginBottom: '0.25rem' }}>
                                        {selectedOrderItem.productName}
                                    </div>
                                    <div style={{ fontSize: '0.875rem', color: '#6b7280' }}>
                                        {selectedOrderItem.quantity}개 × {selectedOrderItem.productPrice?.toLocaleString()}원
                                    </div>
                                </div>
                            )}

                            <form onSubmit={handleSubmitReview}>
                                {/* 평점 */}
                                <div style={{ marginBottom: '1.5rem' }}>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
                                        평점
                                    </label>
                                    <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                                        <input
                                            type="range"
                                            min="0.5"
                                            max="5.0"
                                            step="0.5"
                                            value={reviewForm.reviewRating}
                                            onChange={(e) => setReviewForm({
                                                ...reviewForm,
                                                reviewRating: parseFloat(e.target.value)
                                            })}
                                            style={{ flex: 1 }}
                                        />
                                        <span style={{ fontSize: '1.25rem', fontWeight: '600', minWidth: '3rem' }}>
                                            {'⭐'.repeat(Math.floor(reviewForm.reviewRating))} {reviewForm.reviewRating}
                                        </span>
                                    </div>
                                </div>

                                {/* 리뷰 내용 */}
                                <div style={{ marginBottom: '1.5rem' }}>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
                                        리뷰 내용
                                    </label>
                                    <textarea
                                        value={reviewForm.reviewContent}
                                        onChange={(e) => setReviewForm({ ...reviewForm, reviewContent: e.target.value })}
                                        placeholder="상품에 대한 솔직한 리뷰를 작성해주세요."
                                        required
                                        rows="6"
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            resize: 'vertical'
                                        }}
                                    />
                                </div>

                                {/* 버튼 */}
                                <div style={{ display: 'flex', gap: '1rem' }}>
                                    <button
                                        type="submit"
                                        className="btn btn--primary"
                                        style={{ flex: 1 }}
                                    >
                                        리뷰 등록
                                    </button>
                                    <button
                                        type="button"
                                        onClick={handleCloseReviewModal}
                                        className="btn btn--ghost"
                                        style={{ flex: 1 }}
                                    >
                                        취소
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                )}
            </section>
        </main>
    );
};

export default OrderDetailPage;