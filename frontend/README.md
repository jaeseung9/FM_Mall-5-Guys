# FM-Mall Frontend

FM 전자몰 프론트엔드 (React + Vite)

## 설치 및 실행

### 1. 패키지 설치
```bash
cd frontend
npm install
```

### 2. 개발 서버 실행
```bash
npm run dev
```

개발 서버는 `http://localhost:3000`에서 실행됩니다.

### 3. 빌드
```bash
npm run build
```

## 프로젝트 구조

```
frontend/
├─ src/
│  ├─ components/       # 재사용 가능한 컴포넌트
│  │  ├─ Header.jsx
│  │  ├─ Footer.jsx
│  │  ├─ Sidebar.jsx
│  │  └─ ProductCard.jsx
│  ├─ pages/           # 페이지 컴포넌트
│  │  ├─ MainPage.jsx
│  │  ├─ LoginPage.jsx
│  │  └─ SignupPage.jsx
│  ├─ services/        # API 호출 로직
│  │  └─ api.js
│  ├─ styles.css       # 전역 스타일
│  ├─ App.jsx          # 메인 앱 컴포넌트
│  └─ main.jsx         # 진입점
├─ public/             # 정적 파일
├─ index.html          # HTML 템플릿
├─ vite.config.js      # Vite 설정
└─ package.json        # 의존성 관리

```

## 주요 기능

### 1. 메인 페이지
- 상품 목록 표시 (MySQL DB 연동)
- 카테고리 필터링
- 브랜드 필터링
- 가격대 필터링
- 정렬 기능 (추천순, 가격순, 신상품순)

### 2. 인증
- 로그인 (JWT 토큰 기반)
- 회원가입
- 자동 로그인 유지 (localStorage)

### 3. 권한 관리
- 일반 사용자 / 관리자 구분
- 관리자 페이지 접근 제어

## API 연동

백엔드 API는 `/api` 경로로 프록시됩니다.
- 개발 환경: `http://localhost:3000/api` → `http://localhost:8080`
- Vite 프록시 설정이 자동으로 처리합니다.

### API 엔드포인트

**인증**
- POST `/api/User/login` - 로그인
- POST `/api/User/signup` - 회원가입
- GET `/api/User/myFindOne` - 내 정보 조회

**상품**
- GET `/api/Product/findAll` - 전체 상품 조회
- GET `/api/Product/findOne/{productId}` - 상품 상세
- GET `/api/Product/findByCategory/{categoryId}` - 카테고리별 상품

**카테고리**
- GET `/api/ColumnCategory/findAll` - 전체 카테고리 조회

## 환경 변수

필요시 `.env` 파일 생성:
```
VITE_API_BASE_URL=http://localhost:8080
```

## 백엔드 연동 확인

1. 백엔드 서버가 8080 포트에서 실행 중인지 확인
2. CORS 설정이 올바른지 확인
3. JWT 토큰이 정상적으로 발급되는지 확인
