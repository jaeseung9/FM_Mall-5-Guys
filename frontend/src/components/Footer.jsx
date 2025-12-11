import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Footer = () => {
    const [isAdmin, setIsAdmin] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const userData = localStorage.getItem('user');
        console.log('Footer - localStorage user:', userData);  // ✅ 디버깅

        if (userData) {
            const user = JSON.parse(userData);
            console.log('Footer - parsed user:', user);  // ✅ 디버깅
            console.log('Footer - user.role:', user.role);  // ✅ 디버깅
            setIsAdmin(user.role === 'ADMIN');
        }
    }, []);

    const handleAdminClick = () => {
        const userData = localStorage.getItem('user');
        console.log('Admin 버튼 클릭 - userData:', userData);  // ✅ 디버깅

        if (userData) {
            const user = JSON.parse(userData);
            console.log('Admin 버튼 클릭 - user:', user);  // ✅ 디버깅
            console.log('Admin 버튼 클릭 - role:', user.role);  // ✅ 디버깅

            if (user.role === 'ADMIN') {
                navigate('/admin');
            } else {
                alert('관리자 권한이 필요합니다.');
            }
        } else {
            alert('로그인이 필요합니다.');
            navigate('/login');
        }
    };

    return (
        <footer className="footer">
            <div className="footer__inner">
                <div className="footer__brand">
                    <span className="logo-main">FM</span>
                    <span className="logo-sub">전자몰</span>
                </div>
                <div className="footer__info">
                    <p>고객센터: 1588-0000 | 평일 09:00 ~ 18:00</p>
                    <p>사업자등록번호: 000-00-00000 | 대표: 홍길동</p>
                    <p>© 2025 FM 전자몰. All rights reserved.</p>
                </div>
                <div style={{ marginLeft: 'auto' }}>
                    <button
                        className="btn btn--ghost"
                        onClick={handleAdminClick}
                        style={{ fontSize: '0.85rem' }}
                    >
                        {isAdmin ? '관리자 페이지' : '관리자'}
                    </button>
                </div>
            </div>
        </footer>
    );
};

export default Footer;