import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminAPI, productAPI } from '../services/api';

const AdminPage = () => {
    const [stats, setStats] = useState({
        totalUsers: 0,
        totalProducts: 0,
        activeProducts: 0,
        outOfStockProducts: 0
    });
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        loadDashboardData();
    }, []);

    const loadDashboardData = async () => {
        try {
            setLoading(true);

            // 사용자 통계
            const usersResponse = await adminAPI.getAllUsers();
            const users = usersResponse.data || [];

            // 상품 통계
            const productsResponse = await productAPI.getAllProducts();
            const products = productsResponse.data || [];

            setStats({
                totalUsers: users.length,
                totalProducts: products.length,
                activeProducts: products.filter(p => p.productStatus === 'ACTIVE').length,
                outOfStockProducts: products.filter(p => p.productStatus === 'OUT_OF_STOCK').length
            });
        } catch (error) {
            console.error('대시보드 데이터 로딩 실패:', error);
        } finally {
            setLoading(false);
        }
    };

    const menuItems = [
        {
            title: '상품 관리',
            description: '상품 등록, 수정, 삭제',
            icon: '📦',
            path: '/admin/products',
            color: '#3b82f6',
            stats: `${stats.totalProducts}개 상품`
        },
        {
            title: '사용자 관리',
            description: '사용자 조회 및 관리',
            icon: '👥',
            path: '/admin/users',
            color: '#10b981',
            stats: `${stats.totalUsers}명 사용자`
        },
    /*    {
            title: '카테고리 관리',
            description: '카테고리 설정',
            icon: '📂',
            path: '/admin/categories',
            color: '#f59e0b',
            stats: '카테고리 설정'
        },*/
    /*    {
            title: '브랜드 관리',
            description: '브랜드 등록 및 관리',
            icon: '🏷️',
            path: '/admin/brands',
            color: '#8b5cf6',
            stats: '브랜드 설정'
        },*/
        {
            title: '주문 관리',
            description: '주문 내역 조회',
            icon: '📋',
            path: '/admin/orders',
            color: '#ef4444',
            stats: '주문 처리'
        },
    /*    {
            title: '통계 대시보드',
            description: '매출 및 통계 확인',
            icon: '📊',
            path: '/admin/dashboard',
            color: '#06b6d4',
            stats: '실시간 통계'
        }*/
    ];

    if (loading) {
        return (
            <div style={{ textAlign: 'center', padding: '3rem' }}>
                <p>로딩 중...</p>
            </div>
        );
    }

    return (
        <main className="main" style={{ gridTemplateColumns: '1fr', maxWidth: '1200px' }}>
            <div style={{ backgroundColor: '#ffffff', borderRadius: '1rem', padding: '2rem', border: '1px solid #e5e7eb' }}>
                <div style={{ marginBottom: '2rem' }}>
                    <h1 style={{ fontSize: '1.75rem', fontWeight: '700', marginBottom: '0.5rem' }}>
                        관리자 대시보드
                    </h1>
                    <p style={{ color: '#6b7280', fontSize: '0.95rem' }}>
                        FM Mall 관리 시스템
                    </p>
                </div>

                {/* 통계 카드 */}
                <div style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                    gap: '1rem',
                    marginBottom: '2rem'
                }}>
                    <div style={{
                        padding: '1rem',
                        backgroundColor: '#f0f9ff',
                        borderRadius: '0.5rem',
                        border: '1px solid #bae6fd'
                    }}>
                        <div style={{ fontSize: '0.875rem', color: '#0369a1' }}>전체 상품</div>
                        <div style={{ fontSize: '1.5rem', fontWeight: '700', color: '#0c4a6e' }}>
                            {stats.totalProducts}개
                        </div>
                    </div>

                    <div style={{
                        padding: '1rem',
                        backgroundColor: '#faf5ff',
                        borderRadius: '0.5rem',
                        border: '1px solid #e9d5ff'
                    }}>
                        <div style={{ fontSize: '0.875rem', color: '#7c3aed' }}>전체 사용자</div>
                        <div style={{ fontSize: '1.5rem', fontWeight: '700', color: '#4c1d95' }}>
                            {stats.totalUsers}명
                        </div>
                    </div>
                </div>

                {/* 메뉴 그리드 */}
                <div style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fill, minmax(250px, 1fr))',
                    gap: '1rem'
                }}>
                    {menuItems.map((item, index) => (
                        <div
                            key={index}
                            onClick={() => navigate(item.path)}
                            style={{
                                padding: '1.5rem',
                                backgroundColor: '#ffffff',
                                border: '2px solid #e5e7eb',
                                borderRadius: '0.75rem',
                                cursor: 'pointer',
                                transition: 'all 0.2s',
                                ':hover': {
                                    borderColor: item.color,
                                    transform: 'translateY(-2px)',
                                    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.1)'
                                }
                            }}
                            onMouseEnter={(e) => {
                                e.currentTarget.style.borderColor = item.color;
                                e.currentTarget.style.transform = 'translateY(-2px)';
                                e.currentTarget.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.1)';
                            }}
                            onMouseLeave={(e) => {
                                e.currentTarget.style.borderColor = '#e5e7eb';
                                e.currentTarget.style.transform = 'translateY(0)';
                                e.currentTarget.style.boxShadow = 'none';
                            }}
                        >
                            <div style={{
                                display: 'flex',
                                alignItems: 'center',
                                marginBottom: '0.75rem'
                            }}>
                                <span style={{
                                    fontSize: '1.75rem',
                                    marginRight: '0.75rem'
                                }}>
                                    {item.icon}
                                </span>
                                <div>
                                    <h3 style={{
                                        fontSize: '1.125rem',
                                        fontWeight: '600',
                                        marginBottom: '0.25rem'
                                    }}>
                                        {item.title}
                                    </h3>
                                    <p style={{
                                        fontSize: '0.875rem',
                                        color: '#6b7280'
                                    }}>
                                        {item.description}
                                    </p>
                                </div>
                            </div>
                            <div style={{
                                fontSize: '0.875rem',
                                color: item.color,
                                fontWeight: '500',
                                paddingTop: '0.5rem',
                                borderTop: '1px solid #f3f4f6'
                            }}>
                                {item.stats}
                            </div>
                        </div>
                    ))}
                </div>

                {/* 빠른 작업 버튼들 */}
                <div style={{
                    marginTop: '2rem',
                    paddingTop: '2rem',
                    borderTop: '1px solid #e5e7eb',
                    display: 'flex',
                    gap: '1rem',
                    flexWrap: 'wrap'
                }}>
                    <button
                        onClick={() => navigate('/')}
                        className="btn btn--ghost"
                    >
                        메인 페이지로
                    </button>
                </div>
            </div>
        </main>
    );
};

export default AdminPage;