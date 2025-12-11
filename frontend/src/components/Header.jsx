import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [user, setUser] = useState(null);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    const userData = localStorage.getItem('user');
    if (token && userData) {
      setIsLoggedIn(true);
      setUser(JSON.parse(userData));
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setIsLoggedIn(false);
    setUser(null);
    navigate('/');
  };

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search?q=${encodeURIComponent(searchQuery)}`);
    }
  };

  return (
    <header className="header">
      <div className="header__logo">
        <Link to="/">
          <span className="logo-main">FM</span>
          <span className="logo-sub">전자몰</span>
        </Link>
      </div>

      <nav className="nav">
        <Link to="/" className="nav__link nav__link--active">홈</Link>
       {/* <Link to="/products/new" className="nav__link">신상품</Link>
        <Link to="/products/best" className="nav__link">베스트</Link>
        <Link to="/events" className="nav__link">기획전</Link>
        <Link to="/support" className="nav__link">고객센터</Link>*/}
      </nav>

      <div className="header__actions">
        <form className="search" onSubmit={handleSearch}>
          <input 
            type="text" 
            className="search__input" 
            placeholder="상품명을 입력하세요"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </form>
        
        {isLoggedIn ? (
          <>
            <span style={{ fontSize: '0.9rem', color: '#4b5563' }}>
              {user?.name || user?.loginId}님
            </span>
              <Link to="/orders">
                  <button className="btn btn--ghost">주문내역</button>
              </Link>
            <Link to="/mypage">
              <button className="btn btn--ghost">마이페이지</button>
            </Link>
              <Link to="/cart">
                  <button className="btn btn--ghost">장바구니</button>
              </Link>
            <button className="btn btn--ghost" onClick={handleLogout}>
              로그아웃
            </button>
          </>
        ) : (
          <>
            <Link to="/login">
              <button className="btn btn--ghost">로그인</button>
            </Link>
            <Link to="/signup">
              <button className="btn btn--primary">회원가입</button>
            </Link>
          </>
        )}
      </div>
    </header>
  );
};

export default Header;
