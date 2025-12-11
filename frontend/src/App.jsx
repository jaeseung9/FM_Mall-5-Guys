import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Iridescence from './components/background/Iridescence';
import Header from './components/Header';
import MainPage from './pages/MainPage';
import LoginPage from './pages/LoginPage';
import CartPage from './pages/CartPage';
import OrderDetailPage from './pages/OrderDetailPage';
import OrderListPage from './pages/OrderListPage';
import MyPage from './pages/MyPage';
import WishListPage from './pages/WishListPage';
import AdminPage from './pages/AdminPage';
import AdminProductPage from './pages/AdminProductPage';
import AdminUserPage from './pages/AdminUserPage';
import SignupPage from "./pages/SignupPage";
import Footer from "./components/Footer";
import CartCheckoutPage from "./pages/CartCheckoutPage";

function App() {
    return (
        <Router>
            <div className="iridescence-background">
                <Iridescence
                    color={[0.55, 0.6, 0.85]}
                    mouseReact={false}
                    amplitude={1.0}
                    speed={1.0}
                />
            </div>

            <div className="app-content">
                <Header />
                <Routes>
                    <Route path="/" element={<MainPage />} />
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/signup" element={<SignupPage />} />
                    <Route path="/cart" element={<CartPage />} />
                    <Route path="/cart/checkout" element={<CartCheckoutPage />} />
                    <Route path="/orders" element={<OrderListPage />} />
                    <Route path="/orders/:orderId" element={<OrderDetailPage />} />
                    <Route path="/mypage" element={<MyPage />} />
                    <Route path="/wishlist" element={<WishListPage />} />
                    <Route path="/admin" element={<AdminPage />} />
                    <Route path="/admin/products" element={<AdminProductPage />} />
                    <Route path="/admin/users" element={<AdminUserPage />} />
                </Routes>
                <Footer />
            </div>


        </Router>
    );
}

export default App;