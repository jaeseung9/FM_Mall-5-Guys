import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});



// 요청 인터셉터 - 토큰 자동 추가
apiClient.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        console.log('🔍 인터셉터 - 토큰:', token); // 👈 디버깅 추가
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
            console.log('✅ Authorization 헤더 추가:', config.headers.Authorization); // 👈 디버깅 추가
        } else {
            console.log('❌ 토큰이 없습니다!'); // 👈 디버깅 추가
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 응답 인터셉터 - 401 에러 시 로그아웃 처리
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 인증 관련 API
export const authAPI = {
    login: (loginId, password) =>
        apiClient.post('/User/login', { loginId, password }),

    signup: (userData) =>
        apiClient.post('/User/signup', userData),

    getMyInfo: () =>
        apiClient.get('/User/myFindOne'),

    updateUser: (userData) =>
        apiClient.put('/User/modify', userData),

    deleteUser: (data) =>
        apiClient.delete('/User/delete', { data }),
};
// 상품 관련 API
export const productAPI = {
  getAllProducts: () => 
    apiClient.get('/Product/findAll'),
  
  getProductById: (productId) => 
    apiClient.get(`/Product/findOne/${productId}`),
  
  getProductsByCategory: (categoryId) => 
    apiClient.get(`/Product/findByCategory/${categoryId}`),
};

// 카테고리 관련 API
export const categoryAPI = {
    // 전체 카테고리 조회
    getAllCategories: () =>
        apiClient.get('/Category/findAll'),

    // 전체 브랜드 조회
    getAllBrands: () =>
        apiClient.get('/Brand/findAll'),

    // 카테고리별 하위 카테고리 조회
    getRowCategories: (categoryId) =>
        apiClient.get(`/RowCategory/findByCategoryId/${categoryId}`),
};

// 주소 관련 API
export const addressAPI = {
  getMyAddresses: () => 
    apiClient.get('/Address/findAll'),
  
  addAddress: (addressData) => 
    apiClient.post('/Address/insert', addressData),
  
  updateAddress: (addressId, addressData) => 
    apiClient.put(`/Address/modify/${addressId}`, addressData),
  
  deleteAddress: (addressId) => 
    apiClient.delete(`/Address/delete/${addressId}`),
};

// 결제 수단 관련 API
export const paymentAPI = {
  getMyPayments: () => 
    apiClient.get('/Payment/findAll'),
  
  addPayment: (paymentData) => 
    apiClient.post('/Payment/insert', paymentData),
  
  updatePayment: (paymentMethodId, paymentData) => 
    apiClient.put(`/Payment/modify/${paymentMethodId}`, paymentData),
  
  deletePayment: (paymentMethodId) => 
    apiClient.delete(`/Payment/delete/${paymentMethodId}`),
};

// 관리자 API
export const adminAPI = {
  getAllUsers: () => 
    apiClient.get('/Admin/User/findAll'),
  
  getUserById: (userId) => 
    apiClient.get(`/Admin/User/findOne/${userId}`),
  
  deleteUser: (userId) => 
    apiClient.delete(`/Admin/User/delete/${userId}`),
};

export const adminProductAPI = {
    // 상품 등록
    createProduct: (productData) =>
        apiClient.post('/Admin/Product/insert', productData),

    // 상품 수정
    updateProduct: (productId, productData) =>
        apiClient.put(`/Admin/Product/modify/${productId}`, productData),

    // 상품 삭제
    deleteProduct: (productId) =>
        apiClient.delete(`/Admin/Product/delete/${productId}`),

    // ✅ 관리자용 브랜드 전체 조회 (apiClient 사용)
    getAllBrands: () =>
        apiClient.get('/Admin/Brand/findAll'),

    // ✅ 관리자용 카테고리별 하위 카테고리 조회 (apiClient 사용)
    getRowCategoriesByCategoryId: (categoryId) =>
        apiClient.get(`/Admin/RowCategory/findByCategoryId/${categoryId}`),
};

// 장바구니 관련 API - api.js에 추가할 코드
export const cartAPI = {
    // 장바구니 조회
    getCart: () =>
        apiClient.get('/Cart/findAll'),

    // 장바구니에 상품 추가
    addToCart: (data) =>
        apiClient.post('/Cart/insert', data),

    // 장바구니 상품 수량 변경
    updateCartItem: (cartItemId, data) =>
        apiClient.put(`/Cart/modify/${cartItemId}`, data),

    // 장바구니 상품 삭제
    removeCartItem: (cartItemId) =>
        apiClient.delete(`/Cart/delete/${cartItemId}`),

    // 장바구니 전체 삭제
    clearCart: () =>
        apiClient.delete('/Cart/deleteAll'),
};

// 주문 관련 API
export const orderAPI = {
    // 즉시 주문 (단일 상품)
    createOrder: (orderData) =>
        apiClient.post('/Order/insert', orderData),

    // 장바구니 주문
    createOrderFromCart: (orderData) =>
        apiClient.post('/Order/insertFromCart', orderData),

    // 내 주문 목록
    getMyOrders: () =>
        apiClient.get('/Order/findAll'),

    // 주문 상세
    getOrderDetail: (orderId) =>
        apiClient.get(`/Order/findOne/${orderId}`),

    // 상품별 주문 내역
    getOrdersByProduct: (productId) =>
        apiClient.get(`/Order/findByProduct/${productId}`),

    // 주문 취소
    cancelOrder: (orderId) =>
        apiClient.put(`/Order/cancel/${orderId}`),
};

// 위시리스트 관련 API
export const wishlistAPI = {
    // 내 위시리스트 목록
    getMyWishlists: (curPage = 1) =>
        apiClient.get(`/WishList/findByUser/me?curPage=${curPage}`),

    // 위시리스트 단건 조회
    getWishlistById: (wishListId) =>
        apiClient.get(`/WishList/findOne/${wishListId}`),

    // 위시리스트 토글 (추가/삭제)
    toggleWishlist: (data) =>
        apiClient.post('/WishList/toggle', data),

    // 위시리스트 삭제
    deleteWishlist: (wishListId) =>
        apiClient.delete(`/WishList/delete/${wishListId}`),
};

// 환불 관련 API
export const refundAPI = {
    // 환불 신청
    createRefund: (refundData) =>
        apiClient.post('/Refund/insert', refundData),

    // 내 환불 내역
    getMyRefunds: () =>
        apiClient.get('/Refund/findAll'),

    // 환불 상세
    getRefundDetail: (refundId) =>
        apiClient.get(`/Refund/findOne/${refundId}`),

    // 상품별 환불 내역
    getRefundsByProduct: (productId) =>
        apiClient.get(`/Refund/findByProduct/${productId}`),
};

// 리뷰 관련 API
export const reviewAPI = {
    // 내 리뷰 목록
    getMyReviews: (curPage = 1) =>
        apiClient.get(`/Review/findByUser/me?curPage=${curPage}`),

    // 리뷰 단건 조회
    getReviewById: (reviewId) =>
        apiClient.get(`/Review/findOne/${reviewId}`),

    // 주문 상품별 리뷰 조회
    getReviewByOrderItem: (orderItemId) =>
        apiClient.get(`/Review/findByOrderItem/${orderItemId}`),

    // 리뷰 작성
    createReview: (reviewData) =>
        apiClient.post('/Review/insert', reviewData),

    // 리뷰 수정
    updateReview: (reviewId, reviewData) =>
        apiClient.put(`/Review/modify/${reviewId}`, reviewData),

    // 리뷰 삭제
    deleteReview: (reviewId) =>
        apiClient.delete(`/Review/delete/${reviewId}`),
};

// 문의 관련 API (Inquiry)
export const inquiryAPI = {
    // 내 문의 목록
    getMyInquiries: (curPage = 1) =>
        apiClient.get(`/Inquiry/findByUser/me?curPage=${curPage}`),

    // 문의 단건 조회
    getInquiryById: (inquiryId) =>
        apiClient.get(`/Inquiry/findOne/${inquiryId}`),

    // 문의 작성
    createInquiry: (inquiryData) =>
        apiClient.post('/Inquiry/insert', inquiryData),

    // 문의 수정
    updateInquiry: (inquiryId, inquiryData) =>
        apiClient.put(`/Inquiry/modify/${inquiryId}`, inquiryData),

    // 문의 삭제
    deleteInquiry: (inquiryId) =>
        apiClient.delete(`/Inquiry/delete/${inquiryId}`),
};

export default apiClient;