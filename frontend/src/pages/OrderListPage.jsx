import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { orderAPI } from '../services/api';

const OrderListPage = () => {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        loadOrders();
    }, []);

    const loadOrders = async () => {
        try {
            const response = await orderAPI.getMyOrders();
            setOrders(response.data || []);
        } catch (error) {
            console.error('주문 목록 조회 실패:', error);
            alert('주문 목록을 불러오는 데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleGoDetail = (orderId) => {
        navigate(`/orders/${orderId}`);
    };

    if (loading) {
        return (
            <main className="main">
                <aside className="sidebar">
                    <div className="sidebar__section">
                        <div className="sidebar__title">주문 메뉴</div>
                        <ul className="sidebar__list">
                            <li>
                                <strong>주문 내역</strong>
                            </li>
                        </ul>
                    </div>
                </aside>
                <section className="content">
                    <div className="order-header">
                        <div>
                            <h1 className="order-header__title">주문 내역</h1>
                            <p className="order-header__subtitle">로딩 중...</p>
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
                            <strong>주문 내역</strong>
                        </li>
                    </ul>
                </div>
            </aside>

            <section className="content">
                <div className="order-header">
                    <div>
                        <h1 className="order-header__title">주문 내역</h1>
                        <p className="order-header__subtitle">
                            최근 주문 내역을 확인할 수 있습니다.
                        </p>
                    </div>
                </div>

                <div className="order-section">
                    {orders.length === 0 ? (
                        <div className="order-empty" style={{ textAlign: 'center' }}>
                            주문 내역이 없습니다.
                        </div>
                    ) : (
                        <div className="order-list">
                            {orders.map((order) => (
                                <button
                                    key={order.orderId}
                                    type="button"
                                    onClick={() => handleGoDetail(order.orderId)}
                                    className="order-list__item"
                                >
                                    <div className="order-list__top">
                    <span className="order-list__id">
                      주문번호 #{order.orderId}
                    </span>
                                        <span className="order-list__date">
                      {order.createdAt?.replace('T', ' ').slice(0, 16)}
                    </span>
                                    </div>
                                    <div className="order-list__products">
                                        {order.productNames && order.productNames.length > 0
                                            ? order.productNames.join(', ')
                                            : '상품 정보 없음'}
                                    </div>
                                    <div className="order-list__meta">
                                        <span>총 수량: {order.totalQuantity}개</span>
                                        <span>
                      총 금액: {order.totalPrice?.toLocaleString()}원
                    </span>
                                    </div>
                                </button>
                            ))}
                        </div>
                    )}
                </div>
            </section>
        </main>
    );
};

export default OrderListPage;
