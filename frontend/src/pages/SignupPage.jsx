import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authAPI } from '../services/api';

const SignupPage = () => {
  const [formData, setFormData] = useState({
    loginId: '',
    password: '',
    passwordConfirm: '',
    userName: '',
    userPhone: ''
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
    
    // 비밀번호 확인
    if (formData.password !== formData.passwordConfirm) {
      setError('비밀번호가 일치하지 않습니다.');
      return;
    }

    try {
      const signupData = {
        loginId: formData.loginId,
        password: formData.password,
        userName: formData.userName,
        userPhone: formData.userPhone
      };

      await authAPI.signup(signupData);
      alert('회원가입이 완료되었습니다.');
      navigate('/login');
    } catch (error) {
      console.error('회원가입 실패:', error);
      setError(error.response?.data?.message || '회원가입에 실패했습니다.');
    }
  };

  return (
    <main className="main" style={{ gridTemplateColumns: '1fr', maxWidth: '520px', margin: '3rem auto' }}>
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
          회원가입
        </h1>
        <p style={{ 
          textAlign: 'center', 
          color: '#6b7280', 
          marginBottom: '2rem',
          fontSize: '0.95rem'
        }}>
          FM 전자몰 회원이 되어보세요
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
          <div style={{ marginBottom: '1rem' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '0.5rem', 
              fontSize: '0.9rem',
              fontWeight: '500'
            }}>
              아이디 *
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

          <div style={{ marginBottom: '1rem' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '0.5rem', 
              fontSize: '0.9rem',
              fontWeight: '500'
            }}>
              비밀번호 *
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

          <div style={{ marginBottom: '1rem' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '0.5rem', 
              fontSize: '0.9rem',
              fontWeight: '500'
            }}>
              비밀번호 확인 *
            </label>
            <input
              type="password"
              name="passwordConfirm"
              value={formData.passwordConfirm}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '0.5rem',
                fontSize: '0.95rem'
              }}
              placeholder="비밀번호를 다시 입력하세요"
            />
          </div>

          <div style={{ marginBottom: '1rem' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '0.5rem', 
              fontSize: '0.9rem',
              fontWeight: '500'
            }}>
              이름 *
            </label>
            <input
              type="text"
              name="userName"
              value={formData.userName}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '0.5rem',
                fontSize: '0.95rem'
              }}
              placeholder="이름을 입력하세요"
            />
          </div>

       {/*   <div style={{ marginBottom: '1rem' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '0.5rem', 
              fontSize: '0.9rem',
              fontWeight: '500'
            }}>
              이메일 *
            </label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '0.5rem',
                fontSize: '0.95rem'
              }}
              placeholder="example@email.com"
            />
          </div>*/}

          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ 
              display: 'block', 
              marginBottom: '0.5rem', 
              fontSize: '0.9rem',
              fontWeight: '500'
            }}>
              전화번호 *
            </label>
            <input
              type="tel"
              name="userPhone"
              value={formData.userPhone}
              onChange={handleChange}
              required
              style={{
                width: '100%',
                padding: '0.75rem',
                border: '1px solid #d1d5db',
                borderRadius: '0.5rem',
                fontSize: '0.95rem'
              }}
              placeholder="010-1234-5678"
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
            회원가입
          </button>
        </form>

        <div style={{ 
          marginTop: '1.5rem', 
          textAlign: 'center',
          fontSize: '0.9rem',
          color: '#6b7280'
        }}>
          이미 계정이 있으신가요?{' '}
          <Link 
            to="/login" 
            style={{ 
              color: '#111827', 
              fontWeight: '500',
              textDecoration: 'underline'
            }}
          >
            로그인
          </Link>
        </div>
      </div>
    </main>
  );
};

export default SignupPage;
