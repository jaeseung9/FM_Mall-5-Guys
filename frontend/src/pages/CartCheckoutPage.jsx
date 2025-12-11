import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { addressAPI, paymentAPI, orderAPI } from '../services/api';

const CartCheckoutPage = () => {
    const [addresses, setAddresses] = useState([]);
    const [payments, setPayments] = useState([]);
    const [selectedAddressId, setSelectedAddressId] = useState(null);
    const [selectedPaymentId, setSelectedPaymentId] = useState(null);
    const [loading, setLoading] = useState(true);

    const navigate = useNavigate();

    useEffect(() => {
        loadCheckoutData();
    }, []);

    const loadCheckoutData = async () => {
        try {
            const [addrRes, payRes] = await Promise.all([
                addressAPI.getMyAddresses(),
                paymentAPI.getMyPayments(),
            ]);

            const addrList = addrRes.data || [];
            const payList = payRes.data || [];

            setAddresses(addrList);
            setPayments(payList);

            // 기본 배송지 선택 (없으면 첫 번째)
            if (addrList.length > 0) {
                const defaultAddr = addrList.find((a) => a.isDefault === 'Y') || addrList[0];
                setSelectedAddressId(defaultAddr.id);
            }

            // 기본 결제수단 선택 (없으면 첫 번째)
            if (payList.length > 0) {
                const defaultPay = payList.find((p) => p.isDefault === true) || payList[0];
                setSelectedPaymentId(defaultPay.id);
            }
        } catch (error) {
            console.error('체크아웃 정보 로딩 실패:', error);
            alert('결제 정보를 불러오는 데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleSubmitOrder = async () => {
        // 배송지/결제수단 존재 체크
        if (!addresses.length) {
            alert('등록된 배송지가 없습니다.\n마이페이지에서 배송지를 먼저 등록해주세요.');
            navigate('/mypage');
            return;
        }
        if (!payments.length) {
            alert('등록된 결제수단이 없습니다.\n마이페이지에서 결제수단을 먼저 등록해주세요.');
            navigate('/mypage');
            return;
        }

        // 선택 체크
        if (!selectedAddressId) {
            alert('배송지를 선택해주세요.');
            return;
        }
        if (!selectedPaymentId) {
            alert('결제수단을 선택해주세요.');
            return;
        }

        if (!window.confirm('선택한 배송지와 결제수단으로 주문을 생성하시겠습니까?')) {
            return;
        }

        try {
            const requestBody = {
                addressId: selectedAddressId,
                paymentMethodId: selectedPaymentId,
            };

            const response = await orderAPI.createOrderFromCart(requestBody);
            const createdOrder = response.data;

            alert(`주문이 생성되었습니다.\n주문번호: ${createdOrder.orderId}`);
            navigate(`/orders/${createdOrder.orderId}`);
        } catch (error) {
            console.error('주문 생성 실패:', error);

            const message =
                error.response?.data?.message ||
                error.response?.data ||
                '주문 생성 중 오류가 발생했습니다.';

            alert(message);
        }
    };

    if (loading) {
        return (
            <main className="main">
                <aside className="sidebar">
                    <div className="sidebar__section">
                        <div className="sidebar__title">주문 단계</div>
                        <ul className="sidebar__list">
                            <li>① 장바구니</li>
                            <li>
                                <strong>② 배송지/결제 선택</strong>
                            </li>
                            <li>③ 주문 완료</li>
                        </ul>
                    </div>
                </aside>

                <section className="content">
                    <div className="order-header">
                        <div>
                            <h1 className="order-header__title">주문/결제</h1>
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
                    <div className="sidebar__title">주문 단계</div>
                    <ul className="sidebar__list">
                        <li>① 장바구니</li>
                        <li>
                            <strong>② 배송지/결제 선택</strong>
                        </li>
                        <li>③ 주문 완료</li>
                    </ul>
                </div>
            </aside>

            <section className="content">
                <div className="order-header">
                    <div>
                        <h1 className="order-header__title">주문/결제</h1>
                        <p className="order-header__subtitle">
                            주문에 사용할 배송지와 결제수단을 선택해주세요.
                        </p>
                    </div>
                    <button onClick={() => navigate('/cart')} className="btn btn--ghost">
                        장바구니로 돌아가기
                    </button>
                </div>

                <div className="order-section">
                    <div className="order-grid-2col">
                        {/* 배송지 선택 */}
                        <section>
                            <div className="order-section__header">
                                <h2 className="order-section__title">배송지 선택</h2>
                                <button
                                    type="button"
                                    className="btn btn--ghost"
                                    onClick={() => navigate('/mypage')}
                                >
                                    배송지 관리
                                </button>
                            </div>

                            {addresses.length === 0 ? (
                                <div className="order-empty">
                                    등록된 배송지가 없습니다.
                                    <br />
                                    마이페이지에서 배송지를 등록한 후 다시 시도해주세요.
                                </div>
                            ) : (
                                <div className="order-choice-list">
                                    {addresses.map((addr) => (
                                        <label
                                            key={addr.id}
                                            className={
                                                'order-choice ' +
                                                (selectedAddressId === addr.id ? 'order-choice--selected' : '')
                                            }
                                        >
                                            <input
                                                type="radio"
                                                name="address"
                                                value={addr.id}
                                                checked={selectedAddressId === addr.id}
                                                onChange={() => setSelectedAddressId(addr.id)}
                                            />
                                            <div className="order-choice__main">
                                                <div className="order-choice__header">
                                                    <span className="order-choice__name">{addr.receiverName}</span>
                                                    <span className="order-choice__sub">({addr.receiverPhone})</span>
                                                    {addr.isDefault === 'Y' && (
                                                        <span className="order-choice__badge">기본</span>
                                                    )}
                                                </div>
                                                <div className="order-choice__sub">
                                                    ({addr.zipcode}) {addr.address1} {addr.address2}
                                                </div>
                                            </div>
                                        </label>
                                    ))}
                                </div>
                            )}
                        </section>

                        {/* 결제수단 선택 */}
                        <section>
                            <div className="order-section__header">
                                <h2 className="order-section__title">결제수단 선택</h2>
                                <button
                                    type="button"
                                    className="btn btn--ghost"
                                    onClick={() => navigate('/mypage')}
                                >
                                    결제수단 관리
                                </button>
                            </div>

                            {payments.length === 0 ? (
                                <div className="order-empty">
                                    등록된 결제수단이 없습니다.
                                    <br />
                                    마이페이지에서 결제수단을 등록한 후 다시 시도해주세요.
                                </div>
                            ) : (
                                <div className="order-choice-list">
                                    {payments.map((pm) => (
                                        <label
                                            key={pm.id}
                                            className={
                                                'order-choice ' +
                                                (selectedPaymentId === pm.id ? 'order-choice--selected' : '')
                                            }
                                        >
                                            <input
                                                type="radio"
                                                name="payment"
                                                value={pm.id}
                                                checked={selectedPaymentId === pm.id}
                                                onChange={() => setSelectedPaymentId(pm.id)}
                                            />
                                            <div className="order-choice__main">
                                                <div className="order-choice__header">
                                                    <span className="order-choice__name">{pm.cardCompany}</span>
                                                    {pm.isDefault && (
                                                        <span className="order-choice__badge">기본</span>
                                                    )}
                                                </div>
                                                <div className="order-choice__sub">{pm.maskedCardNumber}</div>
                                            </div>
                                        </label>
                                    ))}
                                </div>
                            )}
                        </section>
                    </div>

                    <div className="order-actions">
                        <button
                            type="button"
                            className="btn btn--ghost"
                            onClick={() => navigate('/cart')}
                        >
                            취소
                        </button>
                        <button
                            type="button"
                            className="btn btn--primary"
                            onClick={handleSubmitOrder}
                        >
                            주문 확정
                        </button>
                    </div>
                </div>
            </section>
        </main>
    );
};

export default CartCheckoutPage;
