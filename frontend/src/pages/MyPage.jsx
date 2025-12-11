import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI, addressAPI, paymentAPI, reviewAPI } from '../services/api';

const MyPage = () => {
    const [loading, setLoading] = useState(true);
    const [activeTab, setActiveTab] = useState('info');
    const [userInfo, setUserInfo] = useState({});
    const [addresses, setAddresses] = useState([]);
    const [payments, setPayments] = useState([]);
    const [reviews, setReviews] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [editForm, setEditForm] = useState({});
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);

    // ✅ 배송지 추가 폼
    const [showAddressForm, setShowAddressForm] = useState(false);
    const [addressForm, setAddressForm] = useState({
        receiverName: '',
        receiverPhone: '',
        zipcode: '',
        address1: '',
        address2: '',
        isDefault: 'N',
    });

    // ✅ 배송지 수정 관련 상태
    const [editingAddress, setEditingAddress] = useState(null);
    const [showAddressEditModal, setShowAddressEditModal] = useState(false);
    const [addressEditForm, setAddressEditForm] = useState({
        receiverName: '',
        receiverPhone: '',
        zipcode: '',
        address1: '',
        address2: '',
        isDefault: 'N',
    });

    // ✅ 결제 수단 추가 폼
    const [showPaymentForm, setShowPaymentForm] = useState(false);
    const [paymentForm, setPaymentForm] = useState({
        cardCompany: '',
        maskedCardNumber: '',
        isDefault: false,
    });

    // ✅ 결제 수단 수정 관련 상태
    const [editingPayment, setEditingPayment] = useState(null);
    const [showPaymentEditModal, setShowPaymentEditModal] = useState(false);
    const [paymentEditForm, setPaymentEditForm] = useState({
        cardCompany: '',
        maskedCardNumber: '',
        isDefault: false,
    });

    // ✅ 리뷰 수정 관련 상태
    const [showReviewEditModal, setShowReviewEditModal] = useState(false);
    const [editingReview, setEditingReview] = useState(null);
    const [reviewEditForm, setReviewEditForm] = useState({
        reviewRating: 5.0,
        reviewContent: ''
    });

    const navigate = useNavigate();

    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) {
            alert('로그인이 필요합니다.');
            navigate('/login');
            return;
        }

        loadUserInfo();
        loadAddresses();
        loadPayments();
    }, []);

    useEffect(() => {
        if (activeTab === 'reviews') {
            loadReviews();
        }
    }, [activeTab, currentPage]);

    const loadUserInfo = async () => {
        try {
            setLoading(true);
            const response = await authAPI.getMyInfo();
            setUserInfo(response.data);
            setEditForm(response.data);
        } catch (error) {
            console.error('사용자 정보 로딩 실패:', error);
            if (error.response?.status === 401) {
                alert('로그인이 필요합니다.');
                navigate('/login');
            }
        } finally {
            setLoading(false);
        }
    };

    const loadAddresses = async () => {
        try {
            const response = await addressAPI.getMyAddresses();
            setAddresses(response.data || []);
        } catch (error) {
            console.error('주소 로딩 실패:', error);
        }
    };

    const loadPayments = async () => {
        try {
            const response = await paymentAPI.getMyPayments();
            setPayments(response.data || []);
        } catch (error) {
            console.error('결제 수단 로딩 실패:', error);
        }
    };

    const loadReviews = async () => {
        try {
            const response = await reviewAPI.getMyReviews(currentPage);
            setReviews(response.data.content || []);
            setTotalPages(response.data.totalPages || 1);
        } catch (error) {
            console.error('리뷰 로딩 실패:', error);
        }
    };

    // =============================
    // 배송지 추가 폼 관련
    // =============================
    const handleAddressFormChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (name === 'isDefault') {
            setAddressForm((prev) => ({
                ...prev,
                isDefault: checked ? 'Y' : 'N',
            }));
            return;
        }

        setAddressForm((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmitAddress = async (e) => {
        e.preventDefault();
        try {
            await addressAPI.addAddress(addressForm);
            alert('배송지가 추가되었습니다.');
            setShowAddressForm(false);
            setAddressForm({
                receiverName: '',
                receiverPhone: '',
                zipcode: '',
                address1: '',
                address2: '',
                isDefault: 'N',
            });
            loadAddresses();
        } catch (error) {
            console.error('배송지 추가 실패:', error);
            alert('배송지 추가에 실패했습니다.');
        }
    };

    // =============================
    // 배송지 수정 관련
    // =============================
    const handleOpenAddressEditModal = (address) => {
        setEditingAddress(address);
        setAddressEditForm({
            receiverName: address.receiverName,
            receiverPhone: address.receiverPhone,
            zipcode: address.zipcode,
            address1: address.address1,
            address2: address.address2,
            isDefault: address.isDefault,
        });
        setShowAddressEditModal(true);
    };

    const handleCloseAddressEditModal = () => {
        setShowAddressEditModal(false);
        setEditingAddress(null);
        setAddressEditForm({
            receiverName: '',
            receiverPhone: '',
            zipcode: '',
            address1: '',
            address2: '',
            isDefault: 'N',
        });
    };

    const handleAddressEditFormChange = (e) => {
        const { name, value, type, checked } = e.target;

        if (name === 'isDefault') {
            setAddressEditForm((prev) => ({
                ...prev,
                isDefault: checked ? 'Y' : 'N',
            }));
            return;
        }

        setAddressEditForm((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmitAddressEdit = async (e) => {
        e.preventDefault();

        if (!editingAddress) return;

        try {
            await addressAPI.updateAddress(editingAddress.id, addressEditForm);
            alert('배송지가 수정되었습니다.');
            handleCloseAddressEditModal();
            loadAddresses();
        } catch (error) {
            console.error('배송지 수정 실패:', error);
            alert('배송지 수정에 실패했습니다.');
        }
    };

    const handleDeleteAddress = async (addressId) => {
        if (!window.confirm('이 배송지를 삭제하시겠습니까?')) return;

        try {
            await addressAPI.deleteAddress(addressId);
            alert('배송지가 삭제되었습니다.');
            loadAddresses();
        } catch (error) {
            console.error('배송지 삭제 실패:', error);
            alert('배송지 삭제에 실패했습니다.');
        }
    };

    // =============================
    // 결제 수단 추가 폼 관련
    // =============================
    const handlePaymentFormChange = (e) => {
        const { name, value, type, checked } = e.target;

        setPaymentForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    const handleSubmitPayment = async (e) => {
        e.preventDefault();
        try {
            await paymentAPI.addPayment(paymentForm);
            alert('결제 수단이 추가되었습니다.');
            setShowPaymentForm(false);
            setPaymentForm({
                cardCompany: '',
                maskedCardNumber: '',
                isDefault: false,
            });
            loadPayments();
        } catch (error) {
            console.error('결제 수단 추가 실패:', error);
            alert('결제 수단 추가에 실패했습니다.');
        }
    };

    // =============================
    // 결제 수단 수정 관련
    // =============================
    const handleOpenPaymentEditModal = (payment) => {
        setEditingPayment(payment);
        setPaymentEditForm({
            cardCompany: payment.cardCompany,
            maskedCardNumber: payment.maskedCardNumber,
            isDefault: payment.isDefault,
        });
        setShowPaymentEditModal(true);
    };

    const handleClosePaymentEditModal = () => {
        setShowPaymentEditModal(false);
        setEditingPayment(null);
        setPaymentEditForm({
            cardCompany: '',
            maskedCardNumber: '',
            isDefault: false,
        });
    };

    const handlePaymentEditFormChange = (e) => {
        const { name, value, type, checked } = e.target;

        setPaymentEditForm((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }));
    };

    const handleSubmitPaymentEdit = async (e) => {
        e.preventDefault();

        if (!editingPayment) return;

        try {
            await paymentAPI.updatePayment(editingPayment.id, paymentEditForm);
            alert('결제 수단이 수정되었습니다.');
            handleClosePaymentEditModal();
            loadPayments();
        } catch (error) {
            console.error('결제 수단 수정 실패:', error);
            alert('결제 수단 수정에 실패했습니다.');
        }
    };

    const handleDeletePayment = async (paymentMethodId) => {
        if (!window.confirm('이 결제 수단을 삭제하시겠습니까?')) return;

        try {
            await paymentAPI.deletePayment(paymentMethodId);
            alert('결제 수단이 삭제되었습니다.');
            loadPayments();
        } catch (error) {
            console.error('결제 수단 삭제 실패:', error);
            alert('결제 수단 삭제에 실패했습니다.');
        }
    };

    // =============================
    // 사용자 정보 수정 관련
    // =============================
    const handleUpdateInfo = async () => {
        try {
            await authAPI.updateUser(editForm);
            alert('정보가 수정되었습니다.');
            setIsEditing(false);
            loadUserInfo();
        } catch (error) {
            console.error('정보 수정 실패:', error);
            alert('정보 수정에 실패했습니다.');
        }
    };

    const handleDeleteAccount = async () => {
        const password = prompt('계정을 삭제하려면 비밀번호를 입력하세요:');
        if (!password) return;

        if (window.confirm('정말로 계정을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
            try {
                await authAPI.deleteUser({ password });
                alert('계정이 삭제되었습니다.');
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                navigate('/');
            } catch (error) {
                console.error('계정 삭제 실패:', error);
                alert('비밀번호를 확인하세요.');
            }
        }
    };

    // =============================
    // 리뷰 관련
    // =============================
    const handleOpenReviewEditModal = (review) => {
        setEditingReview(review);
        setReviewEditForm({
            reviewRating: review.reviewRating,
            reviewContent: review.reviewContent
        });
        setShowReviewEditModal(true);
    };

    const handleCloseReviewEditModal = () => {
        setShowReviewEditModal(false);
        setEditingReview(null);
        setReviewEditForm({
            reviewRating: 5.0,
            reviewContent: ''
        });
    };

    const handleSubmitReviewEdit = async (e) => {
        e.preventDefault();

        if (!reviewEditForm.reviewContent.trim()) {
            alert('리뷰 내용을 입력해주세요.');
            return;
        }

        try {
            await reviewAPI.updateReview(editingReview.reviewId, reviewEditForm);
            alert('리뷰가 수정되었습니다.');
            handleCloseReviewEditModal();
            loadReviews();
        } catch (error) {
            console.error('리뷰 수정 실패:', error);
            alert('리뷰 수정에 실패했습니다.');
        }
    };

    const handleDeleteReview = async (reviewId) => {
        if (!window.confirm('이 리뷰를 삭제하시겠습니까?')) return;

        try {
            await reviewAPI.deleteReview(reviewId);
            alert('리뷰가 삭제되었습니다.');
            loadReviews();
        } catch (error) {
            console.error('리뷰 삭제 실패:', error);
            alert('리뷰 삭제에 실패했습니다.');
        }
    };

    if (loading) {
        return (
            <div style={{ textAlign: 'center', padding: '3rem' }}>
                <p>로딩 중...</p>
            </div>
        );
    }

    return (
        <main className="main" style={{ gridTemplateColumns: '1fr', maxWidth: '1000px' }}>
            <div style={{ backgroundColor: '#ffffff', borderRadius: '1rem', padding: '2rem', border: '1px solid #e5e7eb' }}>
                <h1 style={{ fontSize: '1.75rem', fontWeight: '700', marginBottom: '0.5rem' }}>
                    마이페이지
                </h1>
                <p style={{ color: '#6b7280', marginBottom: '2rem', fontSize: '0.95rem' }}>
                    내 정보 및 설정 관리
                </p>

                {/* ✅ 탭 메뉴 */}
                <div style={{ display: 'flex', gap: '1rem', marginBottom: '2rem', borderBottom: '2px solid #e5e7eb', flexWrap: 'wrap' }}>
                    <button
                        onClick={() => setActiveTab('info')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: activeTab === 'info' ? '600' : '400',
                            borderBottom: activeTab === 'info' ? '2px solid #111827' : 'none',
                            marginBottom: '-2px'
                        }}
                    >
                        내 정보
                    </button>
                    <button
                        onClick={() => setActiveTab('address')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: activeTab === 'address' ? '600' : '400',
                            borderBottom: activeTab === 'address' ? '2px solid #111827' : 'none',
                            marginBottom: '-2px'
                        }}
                    >
                        배송지 관리
                    </button>
                    <button
                        onClick={() => setActiveTab('payment')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: activeTab === 'payment' ? '600' : '400',
                            borderBottom: activeTab === 'payment' ? '2px solid #111827' : 'none',
                            marginBottom: '-2px'
                        }}
                    >
                        결제 수단
                    </button>
                    <button
                        onClick={() => navigate('/wishlist')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: '400',
                            color: '#111827'
                        }}
                    >
                        ❤️ 위시리스트
                    </button>
                    <button
                        onClick={() => setActiveTab('reviews')}
                        style={{
                            padding: '0.75rem 1.5rem',
                            border: 'none',
                            background: 'none',
                            cursor: 'pointer',
                            fontWeight: activeTab === 'reviews' ? '600' : '400',
                            borderBottom: activeTab === 'reviews' ? '2px solid #111827' : 'none',
                            marginBottom: '-2px'
                        }}
                    >
                        내 리뷰
                    </button>
                </div>

                {/* 내 정보 탭 */}
                {activeTab === 'info' && (
                    <div>
                        {!isEditing ? (
                            <div>
                                <div style={{ marginBottom: '2rem' }}>
                                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.5rem' }}>
                                        <h3 style={{ fontSize: '1.25rem', fontWeight: '600' }}>계정 정보</h3>
                                        <button onClick={() => setIsEditing(true)} className="btn btn--ghost">
                                            수정
                                        </button>
                                    </div>
                                    <div style={{ display: 'grid', gap: '1rem' }}>
                                        <div>
                                            <div style={{ fontWeight: '500', marginBottom: '0.25rem', color: '#6b7280', fontSize: '0.875rem' }}>이메일</div>
                                            <div>{userInfo.userEmail}</div>
                                        </div>
                                        <div>
                                            <div style={{ fontWeight: '500', marginBottom: '0.25rem', color: '#6b7280', fontSize: '0.875rem' }}>이름</div>
                                            <div>{userInfo.userName}</div>
                                        </div>
                                        <div>
                                            <div style={{ fontWeight: '500', marginBottom: '0.25rem', color: '#6b7280', fontSize: '0.875rem' }}>전화번호</div>
                                            <div>{userInfo.userPhone}</div>
                                        </div>
                                    </div>
                                </div>
                                <button onClick={handleDeleteAccount} className="btn btn--outline" style={{ borderColor: '#ef4444', color: '#ef4444' }}>
                                    회원 탈퇴
                                </button>
                            </div>
                        ) : (
                            <div>
                                <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '1.5rem' }}>정보 수정</h3>
                                <div style={{ display: 'grid', gap: '1rem', marginBottom: '1.5rem' }}>
                               {/*     <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>이메일 (변경 불가)</label>
                                        <input
                                            type="email"
                                            value={editForm.userEmail || ''}
                                            disabled
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem',
                                                backgroundColor: '#f3f4f6'
                                            }}
                                        />
                                    </div>*/}
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>이름</label>
                                        <input
                                            type="text"
                                            value={editForm.userName || ''}
                                            onChange={(e) => setEditForm({ ...editForm, userName: e.target.value })}
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem'
                                            }}
                                        />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>전화번호</label>
                                        <input
                                            type="tel"
                                            value={editForm.userPhone || ''}
                                            onChange={(e) => setEditForm({ ...editForm, userPhone: e.target.value })}
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem'
                                            }}
                                        />
                                    </div>
                                </div>
                                <div style={{ display: 'flex', gap: '1rem' }}>
                                    <button onClick={handleUpdateInfo} className="btn btn--primary">
                                        저장
                                    </button>
                                    <button
                                        onClick={() => {
                                            setIsEditing(false);
                                            setEditForm(userInfo);
                                        }}
                                        className="btn btn--ghost"
                                    >
                                        취소
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                )}

                {/* 배송지 관리 탭 */}
                {activeTab === 'address' && (
                    <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: '600' }}>배송지 목록</h3>
                            <button
                                className="btn btn--primary"
                                onClick={() => setShowAddressForm((prev) => !prev)}
                            >
                                {showAddressForm ? '취소' : '+ 배송지 추가'}
                            </button>
                        </div>

                        {/* ✅ 배송지 추가 폼 */}
                        {showAddressForm && (
                            <form
                                onSubmit={handleSubmitAddress}
                                style={{
                                    padding: '1.5rem',
                                    backgroundColor: '#f9fafb',
                                    borderRadius: '0.75rem',
                                    marginBottom: '1.5rem',
                                    border: '1px solid #e5e7eb'
                                }}
                            >
                                <h4 style={{ fontSize: '1.1rem', fontWeight: '600', marginBottom: '1rem' }}>새 배송지 추가</h4>
                                <div style={{ display: 'grid', gap: '1rem' }}>
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>받는 사람</label>
                                        <input
                                            type="text"
                                            name="receiverName"
                                            value={addressForm.receiverName}
                                            onChange={handleAddressFormChange}
                                            required
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem',
                                                fontSize: '0.95rem'
                                            }}
                                        />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>연락처</label>
                                        <input
                                            type="tel"
                                            name="receiverPhone"
                                            value={addressForm.receiverPhone}
                                            onChange={handleAddressFormChange}
                                            required
                                            placeholder="010-0000-0000"
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem',
                                                fontSize: '0.95rem'
                                            }}
                                        />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>우편번호</label>
                                        <input
                                            type="text"
                                            name="zipcode"
                                            value={addressForm.zipcode}
                                            onChange={handleAddressFormChange}
                                            required
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem',
                                                fontSize: '0.95rem'
                                            }}
                                        />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>주소</label>
                                        <input
                                            type="text"
                                            name="address1"
                                            value={addressForm.address1}
                                            onChange={handleAddressFormChange}
                                            required
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem',
                                                fontSize: '0.95rem'
                                            }}
                                        />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>상세주소</label>
                                        <input
                                            type="text"
                                            name="address2"
                                            value={addressForm.address2}
                                            onChange={handleAddressFormChange}
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem',
                                                fontSize: '0.95rem'
                                            }}
                                        />
                                    </div>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                        <input
                                            type="checkbox"
                                            name="isDefault"
                                            checked={addressForm.isDefault === 'Y'}
                                            onChange={handleAddressFormChange}
                                            style={{ width: '1.1rem', height: '1.1rem' }}
                                        />
                                        <label style={{ fontWeight: '500', fontSize: '0.9rem' }}>기본 배송지로 설정</label>
                                    </div>
                                </div>
                                <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.25rem' }}>
                                    <button type="submit" className="btn btn--primary">추가</button>
                                    <button
                                        type="button"
                                        onClick={() => setShowAddressForm(false)}
                                        className="btn btn--ghost"
                                    >
                                        취소
                                    </button>
                                </div>
                            </form>
                        )}

                        {/* 배송지 목록 */}
                        {addresses.length === 0 ? (
                            <p style={{ textAlign: 'center', color: '#6b7280', padding: '2rem' }}>
                                등록된 배송지가 없습니다.
                            </p>
                        ) : (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                {addresses.map((address) => (
                                    <div
                                        key={address.id}
                                        style={{
                                            padding: '1.25rem',
                                            border: '1px solid #e5e7eb',
                                            borderRadius: '0.75rem',
                                            backgroundColor: '#f9fafb'
                                        }}
                                    >
                                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                                            <div style={{ fontWeight: '600', fontSize: '1.05rem' }}>{address.receiverName}</div>
                                            {address.isDefault === 'Y' && (
                                                <span style={{
                                                    padding: '0.125rem 0.5rem',
                                                    backgroundColor: '#dbeafe',
                                                    color: '#1e40af',
                                                    borderRadius: '0.25rem',
                                                    fontSize: '0.75rem',
                                                    fontWeight: '600'
                                                }}>
                                                    기본
                                                </span>
                                            )}
                                        </div>
                                        <div style={{ color: '#6b7280', fontSize: '0.875rem', marginBottom: '0.5rem' }}>
                                            [{address.zipcode}] {address.address1} {address.address2}
                                        </div>
                                        <div style={{ color: '#6b7280', fontSize: '0.875rem', marginBottom: '0.75rem' }}>
                                            {address.receiverPhone}
                                        </div>
                                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                                            <button
                                                onClick={() => handleOpenAddressEditModal(address)}
                                                className="btn btn--ghost"
                                                style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem' }}
                                            >
                                                수정
                                            </button>
                                            <button
                                                onClick={() => handleDeleteAddress(address.id)}
                                                className="btn btn--outline"
                                                style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem', borderColor: '#ef4444', color: '#ef4444' }}
                                            >
                                                삭제
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* 결제 수단 관리 탭 */}
                {activeTab === 'payment' && (
                    <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1.5rem' }}>
                            <h3 style={{ fontSize: '1.25rem', fontWeight: '600' }}>결제 수단 목록</h3>
                            <button
                                className="btn btn--primary"
                                onClick={() => setShowPaymentForm((prev) => !prev)}
                            >
                                {showPaymentForm ? '취소' : '+ 결제 수단 추가'}
                            </button>
                        </div>

                        {/* ✅ 결제 수단 추가 폼 */}
                        {showPaymentForm && (
                            <form
                                onSubmit={handleSubmitPayment}
                                style={{
                                    padding: '1.5rem',
                                    backgroundColor: '#f9fafb',
                                    borderRadius: '0.75rem',
                                    marginBottom: '1.5rem',
                                    border: '1px solid #e5e7eb'
                                }}
                            >
                                <h4 style={{ fontSize: '1.1rem', fontWeight: '600', marginBottom: '1rem' }}>새 결제 수단 추가</h4>
                                <div style={{ display: 'grid', gap: '1rem' }}>
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>카드사</label>
                                        <input
                                            type="text"
                                            name="cardCompany"
                                            value={paymentForm.cardCompany}
                                            onChange={handlePaymentFormChange}
                                            required
                                            placeholder="예: 신한카드, KB국민카드"
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem',
                                                fontSize: '0.95rem'
                                            }}
                                        />
                                    </div>
                                    <div>
                                        <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>카드번호 (마스킹)</label>
                                        <input
                                            type="text"
                                            name="maskedCardNumber"
                                            value={paymentForm.maskedCardNumber}
                                            onChange={handlePaymentFormChange}
                                            required
                                            placeholder="예: 1234-****-****-5678"
                                            style={{
                                                width: '100%',
                                                padding: '0.75rem',
                                                border: '1px solid #d1d5db',
                                                borderRadius: '0.5rem',
                                                fontSize: '0.95rem'
                                            }}
                                        />
                                    </div>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                        <input
                                            type="checkbox"
                                            name="isDefault"
                                            checked={paymentForm.isDefault}
                                            onChange={handlePaymentFormChange}
                                            style={{ width: '1.1rem', height: '1.1rem' }}
                                        />
                                        <label style={{ fontWeight: '500', fontSize: '0.9rem' }}>기본 결제 수단으로 설정</label>
                                    </div>
                                </div>
                                <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.25rem' }}>
                                    <button type="submit" className="btn btn--primary">추가</button>
                                    <button
                                        type="button"
                                        onClick={() => setShowPaymentForm(false)}
                                        className="btn btn--ghost"
                                    >
                                        취소
                                    </button>
                                </div>
                            </form>
                        )}

                        {/* 결제 수단 목록 */}
                        {payments.length === 0 ? (
                            <p style={{ textAlign: 'center', color: '#6b7280', padding: '2rem' }}>
                                등록된 결제 수단이 없습니다.
                            </p>
                        ) : (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                {payments.map((payment) => (
                                    <div
                                        key={payment.id}
                                        style={{
                                            padding: '1.25rem',
                                            border: '1px solid #e5e7eb',
                                            borderRadius: '0.75rem',
                                            backgroundColor: '#f9fafb'
                                        }}
                                    >
                                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                                            <div style={{ fontWeight: '600', fontSize: '1.05rem' }}>{payment.cardCompany}</div>
                                            {payment.isDefault && (
                                                <span style={{
                                                    padding: '0.125rem 0.5rem',
                                                    backgroundColor: '#dbeafe',
                                                    color: '#1e40af',
                                                    borderRadius: '0.25rem',
                                                    fontSize: '0.75rem',
                                                    fontWeight: '600'
                                                }}>
                                                    기본
                                                </span>
                                            )}
                                        </div>
                                        <div style={{ color: '#6b7280', fontSize: '0.875rem', marginBottom: '0.75rem' }}>
                                            {payment.maskedCardNumber}
                                        </div>
                                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                                            <button
                                                onClick={() => handleOpenPaymentEditModal(payment)}
                                                className="btn btn--ghost"
                                                style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem' }}
                                            >
                                                수정
                                            </button>
                                            <button
                                                onClick={() => handleDeletePayment(payment.id)}
                                                className="btn btn--outline"
                                                style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem', borderColor: '#ef4444', color: '#ef4444' }}
                                            >
                                                삭제
                                            </button>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                )}

                {/* 내 리뷰 탭 */}
                {activeTab === 'reviews' && (
                    <div>
                        <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '1.5rem' }}>내가 작성한 리뷰</h3>

                        {reviews.length === 0 ? (
                            <p style={{ textAlign: 'center', color: '#6b7280', padding: '2rem' }}>
                                작성한 리뷰가 없습니다.
                            </p>
                        ) : (
                            <>
                                <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                                    {reviews.map((review) => (
                                        <div
                                            key={review.reviewId}
                                            style={{
                                                padding: '1.25rem',
                                                border: '1px solid #e5e7eb',
                                                borderRadius: '0.75rem',
                                                backgroundColor: '#f9fafb'
                                            }}
                                        >
                                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.75rem' }}>
                                                <div style={{ fontWeight: '600' }}>{review.productName}</div>
                                                <div style={{ color: '#f59e0b', fontSize: '0.9rem' }}>
                                                    ⭐ {review.reviewRating}
                                                </div>
                                            </div>
                                            <div style={{ color: '#374151', marginBottom: '0.75rem', lineHeight: '1.6' }}>
                                                {review.reviewContent}
                                            </div>
                                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                                <div style={{ color: '#9ca3af', fontSize: '0.8rem' }}>
                                                    {new Date(review.createdAt).toLocaleDateString()}
                                                </div>
                                                <div style={{ display: 'flex', gap: '0.5rem' }}>
                                                    <button
                                                        onClick={() => handleOpenReviewEditModal(review)}
                                                        className="btn btn--ghost"
                                                        style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem' }}
                                                    >
                                                        수정
                                                    </button>
                                                    <button
                                                        onClick={() => handleDeleteReview(review.reviewId)}
                                                        className="btn btn--outline"
                                                        style={{ fontSize: '0.875rem', padding: '0.375rem 0.75rem', borderColor: '#ef4444', color: '#ef4444' }}
                                                    >
                                                        삭제
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>

                                {/* 페이지네이션 */}
                                {totalPages > 1 && (
                                    <div style={{ display: 'flex', justifyContent: 'center', gap: '0.5rem', marginTop: '2rem' }}>
                                        <button
                                            onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
                                            disabled={currentPage === 1}
                                            className="btn btn--ghost"
                                            style={{ padding: '0.5rem 1rem' }}
                                        >
                                            이전
                                        </button>
                                        <span style={{ padding: '0.5rem 1rem', fontWeight: '500' }}>
                                            {currentPage} / {totalPages}
                                        </span>
                                        <button
                                            onClick={() => setCurrentPage(p => Math.min(totalPages, p + 1))}
                                            disabled={currentPage === totalPages}
                                            className="btn btn--ghost"
                                            style={{ padding: '0.5rem 1rem' }}
                                        >
                                            다음
                                        </button>
                                    </div>
                                )}
                            </>
                        )}
                    </div>
                )}
            </div>

            {/* ✅ 배송지 수정 모달 */}
            {showAddressEditModal && (
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
                        maxHeight: '90vh',
                        overflowY: 'auto'
                    }}>
                        <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '1.5rem' }}>배송지 수정</h3>
                        <form onSubmit={handleSubmitAddressEdit}>
                            <div style={{ display: 'grid', gap: '1rem' }}>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>받는 사람</label>
                                    <input
                                        type="text"
                                        name="receiverName"
                                        value={addressEditForm.receiverName}
                                        onChange={handleAddressEditFormChange}
                                        required
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem'
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>연락처</label>
                                    <input
                                        type="tel"
                                        name="receiverPhone"
                                        value={addressEditForm.receiverPhone}
                                        onChange={handleAddressEditFormChange}
                                        required
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem'
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>우편번호</label>
                                    <input
                                        type="text"
                                        name="zipcode"
                                        value={addressEditForm.zipcode}
                                        onChange={handleAddressEditFormChange}
                                        required
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem'
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>주소</label>
                                    <input
                                        type="text"
                                        name="address1"
                                        value={addressEditForm.address1}
                                        onChange={handleAddressEditFormChange}
                                        required
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem'
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>상세주소</label>
                                    <input
                                        type="text"
                                        name="address2"
                                        value={addressEditForm.address2}
                                        onChange={handleAddressEditFormChange}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem'
                                        }}
                                    />
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <input
                                        type="checkbox"
                                        name="isDefault"
                                        checked={addressEditForm.isDefault === 'Y'}
                                        onChange={handleAddressEditFormChange}
                                        style={{ width: '1.1rem', height: '1.1rem' }}
                                    />
                                    <label style={{ fontWeight: '500', fontSize: '0.9rem' }}>기본 배송지로 설정</label>
                                </div>
                            </div>
                            <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.5rem' }}>
                                <button type="submit" className="btn btn--primary">저장</button>
                                <button
                                    type="button"
                                    onClick={handleCloseAddressEditModal}
                                    className="btn btn--ghost"
                                >
                                    취소
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* ✅ 결제 수단 수정 모달 */}
            {showPaymentEditModal && (
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
                        maxHeight: '90vh',
                        overflowY: 'auto'
                    }}>
                        <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '1.5rem' }}>결제 수단 수정</h3>
                        <form onSubmit={handleSubmitPaymentEdit}>
                            <div style={{ display: 'grid', gap: '1rem' }}>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>카드사</label>
                                    <input
                                        type="text"
                                        name="cardCompany"
                                        value={paymentEditForm.cardCompany}
                                        onChange={handlePaymentEditFormChange}
                                        required
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem'
                                        }}
                                    />
                                </div>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>카드번호 (마스킹)</label>
                                    <input
                                        type="text"
                                        name="maskedCardNumber"
                                        value={paymentEditForm.maskedCardNumber}
                                        onChange={handlePaymentEditFormChange}
                                        required
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem'
                                        }}
                                    />
                                </div>
                                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                                    <input
                                        type="checkbox"
                                        name="isDefault"
                                        checked={paymentEditForm.isDefault}
                                        onChange={handlePaymentEditFormChange}
                                        style={{ width: '1.1rem', height: '1.1rem' }}
                                    />
                                    <label style={{ fontWeight: '500', fontSize: '0.9rem' }}>기본 결제 수단으로 설정</label>
                                </div>
                            </div>
                            <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.5rem' }}>
                                <button type="submit" className="btn btn--primary">저장</button>
                                <button
                                    type="button"
                                    onClick={handleClosePaymentEditModal}
                                    className="btn btn--ghost"
                                >
                                    취소
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            {/* ✅ 리뷰 수정 모달 */}
            {showReviewEditModal && (
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
                        maxHeight: '90vh',
                        overflowY: 'auto'
                    }}>
                        <h3 style={{ fontSize: '1.25rem', fontWeight: '600', marginBottom: '1.5rem' }}>리뷰 수정</h3>
                        <form onSubmit={handleSubmitReviewEdit}>
                            <div style={{ display: 'grid', gap: '1rem' }}>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>평점</label>
                                    <select
                                        value={reviewEditForm.reviewRating}
                                        onChange={(e) => setReviewEditForm({ ...reviewEditForm, reviewRating: parseFloat(e.target.value) })}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem'
                                        }}
                                    >
                                        <option value="5.0">⭐⭐⭐⭐⭐ (5.0)</option>
                                        <option value="4.0">⭐⭐⭐⭐ (4.0)</option>
                                        <option value="3.0">⭐⭐⭐ (3.0)</option>
                                        <option value="2.0">⭐⭐ (2.0)</option>
                                        <option value="1.0">⭐ (1.0)</option>
                                    </select>
                                </div>
                                <div>
                                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500', fontSize: '0.9rem' }}>리뷰 내용</label>
                                    <textarea
                                        value={reviewEditForm.reviewContent}
                                        onChange={(e) => setReviewEditForm({ ...reviewEditForm, reviewContent: e.target.value })}
                                        required
                                        rows={5}
                                        style={{
                                            width: '100%',
                                            padding: '0.75rem',
                                            border: '1px solid #d1d5db',
                                            borderRadius: '0.5rem',
                                            fontSize: '0.95rem',
                                            resize: 'vertical'
                                        }}
                                    />
                                </div>
                            </div>
                            <div style={{ display: 'flex', gap: '0.75rem', marginTop: '1.5rem' }}>
                                <button type="submit" className="btn btn--primary">저장</button>
                                <button
                                    type="button"
                                    onClick={handleCloseReviewEditModal}
                                    className="btn btn--ghost"
                                >
                                    취소
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </main>
    );
};

export default MyPage;