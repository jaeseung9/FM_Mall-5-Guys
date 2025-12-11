import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminAPI } from '../services/api';

const AdminUserPage = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedUser, setSelectedUser] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        loadUsers();
    }, []);

    const loadUsers = async () => {
        try {
            setLoading(true);
            const response = await adminAPI.getAllUsers();
            setUsers(response.data || []);
        } catch (error) {
            console.error('사용자 목록 로딩 실패:', error);
            alert('사용자 목록을 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleDeleteUser = async (userId, loginId) => {
        if (!window.confirm(`${loginId} 사용자를 삭제하시겠습니까?`)) {
            return;
        }

        try {
            await adminAPI.deleteUser(userId);
            alert('사용자가 삭제되었습니다.');
            loadUsers();
        } catch (error) {
            console.error('사용자 삭제 실패:', error);
            alert('사용자 삭제에 실패했습니다.');
        }
    };

    const handleViewDetail = async (userId) => {
        try {
            const response = await adminAPI.getUserById(userId);
            setSelectedUser(response.data);
        } catch (error) {
            console.error('사용자 상세 정보 로딩 실패:', error);
            alert('사용자 정보를 불러오는데 실패했습니다.');
        }
    };

    // 검색 필터링
    const filteredUsers = users.filter(user =>
        user.loginId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.userName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.userPhone?.includes(searchTerm)
    );

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
                {/* 헤더 */}
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
                    <div>
                        <h1 style={{ fontSize: '1.75rem', fontWeight: '700', marginBottom: '0.5rem' }}>
                            사용자 관리
                        </h1>
                        <p style={{ color: '#6b7280', fontSize: '0.95rem' }}>
                            시스템 사용자 조회 및 관리
                        </p>
                    </div>
                    <button
                        onClick={() => navigate('/admin')}
                        className="btn btn--ghost"
                    >
                        ← 대시보드
                    </button>
                </div>

                {/* 통계 카드 */}
                <div style={{
                    display: 'grid',
                    gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
                    gap: '1rem',
                    marginBottom: '2rem'
                }}>
                    <div style={{ padding: '1rem', backgroundColor: '#f9fafb', borderRadius: '0.5rem' }}>
                        <div style={{ fontSize: '0.875rem', color: '#6b7280' }}>전체 사용자</div>
                        <div style={{ fontSize: '1.25rem', fontWeight: '700' }}>{users.length}명</div>
                    </div>
                    <div style={{ padding: '1rem', backgroundColor: '#fef2f2', borderRadius: '0.5rem' }}>
                        <div style={{ fontSize: '0.875rem', color: '#6b7280' }}>관리자</div>
                        <div style={{ fontSize: '1.25rem', fontWeight: '700', color: '#b91c1c' }}>
                            {users.filter(u => u.role === 'ADMIN').length}명
                        </div>
                    </div>
                    <div style={{ padding: '1rem', backgroundColor: '#eff6ff', borderRadius: '0.5rem' }}>
                        <div style={{ fontSize: '0.875rem', color: '#6b7280' }}>일반 사용자</div>
                        <div style={{ fontSize: '1.25rem', fontWeight: '700', color: '#1e40af' }}>
                            {users.filter(u => u.role === 'USER').length}명
                        </div>
                    </div>
                </div>

                {/* 검색 바 */}
                <div style={{ marginBottom: '1.5rem' }}>
                    <input
                        type="text"
                        placeholder="이름, 아이디, 전화번호로 검색..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        style={{
                            width: '100%',
                            maxWidth: '400px',
                            padding: '0.625rem 1rem',
                            border: '1px solid #d1d5db',
                            borderRadius: '0.5rem',
                            fontSize: '0.875rem'
                        }}
                    />
                </div>

                {/* 사용자 목록 테이블 */}
                <div style={{ overflowX: 'auto' }}>
                    <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                        <thead>
                        <tr style={{ borderBottom: '2px solid #e5e7eb' }}>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>ID</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>로그인 ID</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>이름</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>전화번호</th>
                            <th style={{ padding: '1rem', textAlign: 'left', fontWeight: '600' }}>권한</th>
                            <th style={{ padding: '1rem', textAlign: 'center', fontWeight: '600' }}>관리</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredUsers.length === 0 ? (
                            <tr>
                                <td colSpan="6" style={{ padding: '2rem', textAlign: 'center', color: '#6b7280' }}>
                                    {searchTerm ? '검색 결과가 없습니다.' : '등록된 사용자가 없습니다.'}
                                </td>
                            </tr>
                        ) : (
                            filteredUsers.map((user) => (
                                <tr key={user.id} style={{ borderBottom: '1px solid #f3f4f6' }}>
                                    <td style={{ padding: '1rem' }}>{user.id}</td>
                                    <td style={{ padding: '1rem' }}>{user.loginId}</td>
                                    <td style={{ padding: '1rem' }}>{user.userName}</td>
                                    <td style={{ padding: '1rem' }}>{user.userPhone || '-'}</td>
                                    <td style={{ padding: '1rem' }}>
                                        <span style={{
                                            padding: '0.25rem 0.5rem',
                                            borderRadius: '0.375rem',
                                            fontSize: '0.875rem',
                                            backgroundColor: user.role === 'ADMIN' ? '#fee2e2' : '#dbeafe',
                                            color: user.role === 'ADMIN' ? '#b91c1c' : '#1e40af'
                                        }}>
                                            {user.role}
                                        </span>
                                    </td>
                                    <td style={{ padding: '1rem', textAlign: 'center' }}>
                                        <button
                                            onClick={() => handleViewDetail(user.id)}
                                            className="btn btn--ghost"
                                            style={{ marginRight: '0.5rem', padding: '0.375rem 0.75rem', fontSize: '0.875rem' }}
                                        >
                                            상세
                                        </button>
                                        <button
                                            onClick={() => handleDeleteUser(user.id, user.loginId)}
                                            className="btn btn--outline"
                                            style={{ padding: '0.375rem 0.75rem', fontSize: '0.875rem', borderColor: '#ef4444', color: '#ef4444' }}
                                            disabled={user.role === 'ADMIN'}
                                        >
                                            삭제
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>

                {/* 사용자 상세 정보 모달 */}
                {selectedUser && (
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
                                사용자 상세 정보
                            </h2>

                            <div style={{ marginBottom: '1rem' }}>
                                <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                    사용자 ID
                                </label>
                                <div style={{ fontSize: '1rem' }}>{selectedUser.id}</div>
                            </div>

                            <div style={{ marginBottom: '1rem' }}>
                                <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                    로그인 ID
                                </label>
                                <div style={{ fontSize: '1rem' }}>{selectedUser.loginId}</div>
                            </div>

                            <div style={{ marginBottom: '1rem' }}>
                                <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                    이름
                                </label>
                                <div style={{ fontSize: '1rem' }}>{selectedUser.userName}</div>
                            </div>

                            <div style={{ marginBottom: '1rem' }}>
                                <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                    전화번호
                                </label>
                                <div style={{ fontSize: '1rem' }}>{selectedUser.userPhone || '-'}</div>
                            </div>

                            <div style={{ marginBottom: '1.5rem' }}>
                                <label style={{ display: 'block', fontSize: '0.875rem', color: '#6b7280', marginBottom: '0.25rem' }}>
                                    권한
                                </label>
                                <span style={{
                                    padding: '0.25rem 0.5rem',
                                    borderRadius: '0.375rem',
                                    fontSize: '0.875rem',
                                    backgroundColor: selectedUser.role === 'ADMIN' ? '#fee2e2' : '#dbeafe',
                                    color: selectedUser.role === 'ADMIN' ? '#b91c1c' : '#1e40af'
                                }}>
                                    {selectedUser.role}
                                </span>
                            </div>

                            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                                <button
                                    onClick={() => setSelectedUser(null)}
                                    className="btn btn--primary"
                                >
                                    닫기
                                </button>
                                {selectedUser.role !== 'ADMIN' && (
                                    <button
                                        onClick={() => {
                                            handleDeleteUser(selectedUser.id, selectedUser.loginId);
                                            setSelectedUser(null);
                                        }}
                                        className="btn btn--outline"
                                        style={{ borderColor: '#ef4444', color: '#ef4444' }}
                                    >
                                        사용자 삭제
                                    </button>
                                )}
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </main>
    );
};

export default AdminUserPage;