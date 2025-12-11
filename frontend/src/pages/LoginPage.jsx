import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../services/api';

const LoginPage = () => {
  const [formData, setFormData] = useState({
    loginId: '',
    password: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
    setError('');
  };

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await authAPI.login(formData.loginId, formData.password);

            // ✅ 응답 데이터 구조 확인
            console.log('로그인 응답:', response.data);

            // 토큰 저장
            localStorage.setItem('token', response.data.accessToken);

            // 사용자 정보 저장
            const user = {
                loginId: response.data.loginId,
                role: response.data.role  // "ADMIN" 또는 "USER"
            };

            console.log('저장된 user 정보:', user);  // ✅ 확인용
            localStorage.setItem('user', JSON.stringify(user));

            // 메인 페이지로 이동
            navigate('/');
            window.location.reload();
        } catch (error) {
            console.error('로그인 실패:', error);
            setError('아이디 또는 비밀번호가 일치하지 않습니다.');
        }
    };

  return (
    <main className="main" style={{ gridTemplateColumns: '1fr', maxWidth: '480px', margin: '3rem auto' }}>
      <div style={{ 
        backgroundColor: '#ffffff', 
        borderRadius: '1rem', 
        padding: '2.5rem', 
        border: '1px solid #e5e7eb' 
      }}>
        <h1 style={{ 
          fontSize: '1.75rem', 
          fontWeight: '700', 
          marginBottom: '0.5rem',
          textAlign: 'center'
        }}>
          로그인
        </h1>
        <p style={{ 
          textAlign: 'center', 
          color: '#6b7280', 
          marginBottom: '2rem',
          fontSize: '0.95rem'
        }}>
          FM 전자몰에 오신 것을 환영합니다
        </p>

        {error && (
          <div style={{ 
            backgroundColor: '#fee2e2', 
            color: '#b91c1c', 
            padding: '0.75rem', 
            borderRadius: '0.5rem',
            marginBottom: '1.5rem',
            fontSize: '0.9rem',
            textAlign: 'center'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '0.5rem', 
              fontSize: '0.9rem',
              fontWeight: '500'
            }}>
              아이디
            </label>
            <input
              type="text"
              name="loginId"
              value={formData.loginId}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '0.5rem',
                fontSize: '0.95rem'
              }}
              placeholder="아이디를 입력하세요"
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '0.5rem', 
              fontSize: '0.9rem',
              fontWeight: '500'
            }}>
              비밀번호
            </label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '0.5rem',
                fontSize: '0.95rem'
              }}
              placeholder="비밀번호를 입력하세요"
            />
          </div>

          <button
            type="submit"
            className="btn btn--primary full-width"
            style={{ 
              padding: '0.875rem',
              fontSize: '1rem',
              fontWeight: '500'
            }}
          >
            로그인
          </button>
        </form>

        <div style={{ 
          marginTop: '1.5rem', 
          textAlign: 'center',
          fontSize: '0.9rem',
          color: '#6b7280'
        }}>
          계정이 없으신가요?{' '}
          <Link 
            to="/signup" 
            style={{ 
              color: '#111827', 
              fontWeight: '500',
              textDecoration: 'underline'
            }}
          >
            회원가입
          </Link>
        </div>
      </div>
    </main>
  );
};

export default LoginPage;
