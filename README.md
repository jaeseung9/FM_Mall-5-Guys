<div id="top"></div>

<!-- ──────────────────────────────────────────────────────────────────────────────
  Project Summary
──────────────────────────────────────────────────────────────────────────────── -->
<h2>📌 FM-Mall 인증 및 보안 시스템 (JWT Authentication & Spring Security)</h2>

<ul>
  <li><b>개발 인원</b>: 6인 팀 프로젝트 (백엔드 인증/보안 파트 담당)</li>
  <li><b>개발 기간</b>: 2025.12.01 ~ 2025.12.14</li>
  <li><b>담당 역할</b>: JWT 기반 인증 시스템, Spring Security 설정, 회원가입/로그인 API 개발</li>
  <li>
    🔗 <b>GitHub Repository</b>:
    <a href="[GitHub 링크]" target="_blank" rel="noreferrer">
      [GitHub 링크]
    </a>
  </li>
</ul>

<h3>✅ 주요 개발 내용</h3>
<ul>
  <li><b>JWT 기반 Stateless 인증 시스템</b> 설계 및 구현</li>
  <li><code>JwtTokenProvider</code>를 통한 <b>토큰 생성/검증</b> 로직 구현</li>
  <li><code>JwtAuthorizationFilter</code>로 <b>요청별 토큰 인증 필터</b> 구현</li>
  <li><b>Spring Security 필터 체인</b> 설정 및 권한 기반 접근 제어 (ROLE_USER, ROLE_ADMIN)</li>
  <li><b>프론트엔드 연동</b>: Axios Interceptor를 통한 자동 토큰 관리 및 인증 실패 처리</li>
</ul>

<h3>📚 학습 및 성과</h3>
<ul>
  <li><b>보안 원칙</b>: JWT 토큰 기반 인증의 장단점을 이해하고 실전 적용</li>
  <li><b>Spring Security</b>: 필터 체인의 동작 원리와 커스텀 필터 구현 방법 학습</li>
  <li><b>인증/인가 분리</b>: Authentication과 Authorization의 차이를 명확히 구분하여 설계</li>
  <li><b>프론트엔드 협업</b>: 토큰 기반 인증의 클라이언트-서버 통신 흐름 이해</li>
</ul>

<hr/>

<h1 align="center">🔐 FM-Mall Authentication & Security System</h1>
<p align="center">
  <b>Spring Boot + JWT + Spring Security</b>로 구현한 안전한 인증 시스템<br/>
  토큰 기반 인증 · 권한 제어 · 비밀번호 암호화 · 프론트엔드 연동
</p>

<hr/>

<!-- TL;DR -->
<h2 id="tldr">🧭 TL;DR (빠른 소개)</h2>
<ul>
  <li><b>핵심 기술</b>: Spring Boot 3.x, Spring Security 6.x, JWT (HS256)</li>
  <li><b>주요 기능</b>: 회원가입, 로그인, JWT 토큰 발급/검증, 권한 기반 접근 제어</li>
  <li><b>설계 패턴</b>: Filter Chain Pattern, Provider Pattern, UserDetails 추상화</li>
  <li><b>보안 강화</b>: BCrypt 비밀번호 암호화, Stateless 세션 관리, CORS 설정</li>
  <li>자세한 기술/설계는 아래 <a href="#full">📚 상세 내용</a>에 포함 (접기/펼치기)</li>
</ul>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Table of Contents -->
<h2 id="toc">📚 Table of Contents</h2>
<ol>
  <li><a href="#about">시스템 소개</a></li>
  <li><a href="#architecture">인증 아키텍처</a></li>
  <li><a href="#features">주요 특징</a></li>
  <li><a href="#stack">기술 스택 (요약)</a></li>
  <li><a href="#structure">프로젝트 구조</a></li>
  <li><a href="#api">API 명세</a></li>
  <li><a href="#security">보안 고려사항</a></li>
  <li><a href="#flow">인증 플로우</a></li>
  <li><a href="#learned">학습 내용 (요약)</a></li>
  <li><a href="#full">📚 상세 내용 (전체 본문, 접기/펼치기)</a></li>
  <li><a href="#insights">🌱 느낀점 (전체 본문, 접기/펼치기)</a></li>
</ol>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- About -->
<h2 id="about">1) 시스템 소개</h2>
<p>
  <b>FM-Mall 전자상거래 플랫폼</b>의 인증 및 보안 시스템을 담당했습니다.
  <b>JWT 기반 토큰 인증</b>으로 Stateless한 아키텍처를 구현하고,
  <b>Spring Security</b>를 활용한 세밀한 권한 제어를 통해
  안전하고 확장 가능한 인증 시스템을 설계했습니다.
</p>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Architecture -->
<h2 id="architecture">2) 🏗 인증 아키텍처</h2>
<pre><code>
┌─────────────┐      ┌──────────────────┐      ┌─────────────────┐
│   Client    │─────▶│  UserController  │─────▶│  UserService    │
│  (React)    │◀─────│  (로그인/회원가입) │◀─────│ (비즈니스 로직) │
└─────────────┘      └──────────────────┘      └─────────────────┘
       │                      │                         │
       │ JWT Token            │                         ▼
       │                      │                ┌─────────────────┐
       ▼                      │                │ UserRepository  │
┌─────────────┐              │                │   (JPA/MySQL)   │
│   Axios     │              ▼                └─────────────────┘
│ Interceptor │      ┌──────────────────┐
│ (자동 토큰 추가)│      │JwtTokenProvider │
└─────────────┘      │  (토큰 생성/검증)  │
       │              └──────────────────┘
       │                      ▲
       │                      │
       ▼                      │
┌─────────────────────────────┴──────┐
│   JwtAuthorizationFilter           │
│   (요청별 토큰 검증 및 인증 설정)      │
└────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────┐
│      SecurityContextHolder          │
│   (인증 정보 저장 및 권한 확인)         │
└─────────────────────────────────────┘
</code></pre>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Features -->
<h2 id="features">3) 🚀 주요 특징</h2>
<ul>
  <li>🔑 <b>JWT 토큰 기반 인증</b>: Stateless 아키텍처로 서버 확장성 확보</li>
  <li>🛡️ <b>Spring Security 통합</b>: 필터 체인을 통한 요청별 인증/인가 처리</li>
  <li>🔐 <b>비밀번호 암호화</b>: BCryptPasswordEncoder를 사용한 안전한 저장</li>
  <li>👥 <b>권한 기반 제어</b>: ROLE_USER, ROLE_ADMIN 구분</li>
  <li>🌐 <b>CORS 설정</b>: 프론트엔드와 안전한 통신</li>
  <li>✅ <b>입력값 검증</b>: Jakarta Validation (@Valid) 적용</li>
  <li>⚡ <b>자동 토큰 관리</b>: Axios Interceptor를 통한 클라이언트 측 토큰 처리</li>
</ul>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Stack (summary) -->
<h2 id="stack">4) 🧱 기술 스택 (요약)</h2>
<ul>
  <li><b>Backend Framework</b>: Spring Boot 3.x</li>
  <li><b>Security</b>: Spring Security 6.x</li>
  <li><b>Authentication</b>: JWT (io.jsonwebtoken:jjwt)</li>
  <li><b>Database</b>: MySQL 8.x, Spring Data JPA</li>
  <li><b>Validation</b>: Jakarta Validation API</li>
  <li><b>API Documentation</b>: SpringDoc OpenAPI (Swagger)</li>
  <li><b>Frontend</b>: React + Axios</li>
</ul>
<p><a href="#full">➡ 상세 스택으로 이동</a></p>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Structure -->
<h2 id="structure">5) 📁 프로젝트 구조</h2>
<pre><code>fmmall/
├── src/main/java/com/sesac/fmmall/
│   ├── Security/
│   │   ├── JwtTokenProvider.java          # JWT 생성/검증 핵심 로직
│   │   ├── JwtAuthorizationFilter.java    # 요청별 토큰 인증 필터
│   │   ├── CustomUserDetails.java         # UserDetails 구현체
│   │   └── SecurityConfig.java            # Spring Security 설정
│   ├── Controller/
│   │   └── UserController.java            # 인증 관련 API 엔드포인트
│   ├── Service/
│   │   └── UserService.java               # 회원가입/로그인 비즈니스 로직
│   ├── Repository/
│   │   └── UserRepository.java            # JPA 리포지토리
│   ├── DTO/
│   │   └── User/
│   │       ├── LoginRequestDto.java       # 로그인 요청 DTO
│   │       ├── UserSaveRequestDto.java    # 회원가입 요청 DTO
│   │       ├── TokenResponseDto.java      # 토큰 응답 DTO
│   │       └── UserResponseDto.java       # 사용자 정보 응답 DTO
│   └── Entity/
│       └── User.java                      # 사용자 엔티티
│
└── frontend/
    └── src/
        ├── services/
        │   └── api.js                     # Axios 인스턴스 및 인터셉터
        └── pages/
            ├── LoginPage.jsx              # 로그인 페이지
            └── SignupPage.jsx             # 회원가입 페이지
</code></pre>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- API -->
<h2 id="api">6) 📡 API 명세</h2>
<table>
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>설명</th>
      <th>인증 필요</th>
      <th>권한</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>POST</td>
      <td>/User/signup</td>
      <td>회원가입</td>
      <td>❌</td>
      <td>-</td>
    </tr>
    <tr>
      <td>POST</td>
      <td>/User/login</td>
      <td>로그인 (JWT 발급)</td>
      <td>❌</td>
      <td>-</td>
    </tr>
    <tr>
      <td>GET</td>
      <td>/User/myFindOne</td>
      <td>내 정보 조회</td>
      <td>✅</td>
      <td>USER, ADMIN</td>
    </tr>
    <tr>
      <td>PUT</td>
      <td>/User/modify</td>
      <td>회원정보 수정</td>
      <td>✅</td>
      <td>USER, ADMIN</td>
    </tr>
    <tr>
      <td>DELETE</td>
      <td>/User/delete</td>
      <td>회원탈퇴</td>
      <td>✅</td>
      <td>USER, ADMIN</td>
    </tr>
  </tbody>
</table>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Security -->
<h2 id="security">7) 🔒 보안 고려사항</h2>

<h3>구현된 보안 기능</h3>
<ul>
  <li>✅ <b>비밀번호 암호화</b>: BCryptPasswordEncoder 사용 (단방향 해시)</li>
  <li>✅ <b>Stateless 세션</b>: JWT 토큰 기반으로 서버 메모리 부담 없음</li>
  <li>✅ <b>토큰 검증</b>: 서명(Signature) 검증 및 만료 시간 확인</li>
  <li>✅ <b>CORS 설정</b>: 허용된 Origin만 접근 가능</li>
  <li>✅ <b>입력값 검증</b>: @Valid를 통한 파라미터 유효성 검사</li>
  <li>✅ <b>XSS 방지</b>: Spring Security 기본 헤더 설정</li>
</ul>

<h3>향후 개선 가능 사항</h3>
<ul>
  <li>🔄 <b>Refresh Token</b>: Access Token 만료 시 재발급 메커니즘</li>
  <li>🗄️ <b>토큰 블랙리스트</b>: Redis를 활용한 로그아웃 토큰 무효화</li>
  <li>🌐 <b>OAuth2 연동</b>: 구글/카카오 소셜 로그인</li>
  <li>📊 <b>로그인 시도 제한</b>: Brute Force Attack 방지</li>
  <li>📱 <b>2FA 인증</b>: 이중 인증 시스템 도입</li>
</ul>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Flow -->
<h2 id="flow">8) 🔄 인증 플로우</h2>

<h3>로그인 프로세스</h3>
<pre><code>1. 클라이언트: POST /User/login { loginId, password }
   ↓
2. UserController: LoginRequestDto 검증 (@Valid)
   ↓
3. UserService: 아이디로 사용자 조회
   ↓
4. UserService: BCrypt로 비밀번호 검증
   ↓
5. JwtTokenProvider: JWT 토큰 생성
   - Header: { "alg": "HS256", "typ": "JWT" }
   - Payload: { "sub": userId, "role": "ROLE_USER", "iat": ..., "exp": ... }
   - Signature: HMAC-SHA256(base64(header) + base64(payload), secret)
   ↓
6. UserController: TokenResponseDto 반환
   - accessToken: "eyJhbGc..."
   - tokenType: "Bearer"
   - loginId: "user123"
   - role: "USER"
   ↓
7. 클라이언트: localStorage에 토큰 저장
</code></pre>

<h3>인증된 요청 처리</h3>
<pre><code>1. 클라이언트: GET /User/myFindOne
   - Header: Authorization: Bearer eyJhbGc...
   ↓
2. JwtAuthorizationFilter.doFilterInternal()
   ↓
3. Authorization 헤더에서 "Bearer " 제거하여 토큰 추출
   ↓
4. JwtTokenProvider.validateToken(token)
   - 서명 검증
   - 만료 시간 확인
   ↓
5. 토큰에서 userId 추출
   ↓
6. DB에서 사용자 정보 조회 (UserRepository)
   ↓
7. CustomUserDetails 객체 생성
   ↓
8. SecurityContext에 Authentication 저장
   - Principal: CustomUserDetails
   - Authorities: [ROLE_USER] or [ROLE_ADMIN]
   ↓
9. UserController: SecurityContext에서 인증 정보 확인
   ↓
10. UserService: 비즈니스 로직 수행
   ↓
11. UserController: UserResponseDto 반환
</code></pre>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Learned -->
<h2 id="learned">9) 📖 학습 내용 (요약)</h2>
<ul>
  <li><b>JWT 토큰 구조</b>: Header, Payload, Signature의 역할과 생성/검증 메커니즘</li>
  <li><b>Spring Security 필터 체인</b>: OncePerRequestFilter의 동작 원리와 커스텀 필터 구현</li>
  <li><b>인증 vs 인가</b>: Authentication(누구인가)과 Authorization(무엇을 할 수 있는가)의 명확한 구분</li>
  <li><b>Stateless 설계</b>: 세션 대신 토큰을 사용하는 아키텍처의 장단점</li>
  <li><b>보안 모범 사례</b>: 비밀번호 암호화, CORS, XSS 방지 등 웹 보안 기초</li>
  <li><b>프론트엔드 연동</b>: Axios Interceptor를 통한 토큰 자동 관리 및 에러 처리</li>
</ul>
<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<!-- Full Detail -->
<h2 id="full">10) 📚 상세 내용 (전체 본문, 접기/펼치기)</h2>
<details>
  <summary><b>클릭하여 펼치기</b> — JWT 상세 구현/Security 설정/DTO 구조/프론트엔드 연동</summary>
  <br/>

  <!-- JWT Token Provider 상세 -->
  <h3 id="full-jwt">A. JwtTokenProvider 상세 구현</h3>
  <h4>토큰 생성 (createToken)</h4>
  <pre><code>java
public String createToken(User user) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration); // 예: 24시간
    
    return Jwts.builder()
        .setSubject(String.valueOf(user.getUserId()))        // 사용자 ID
        .claim("role", user.getRole().name())                // 권한 정보
        .setIssuedAt(now)                                    // 발급 시간
        .setExpiration(expiryDate)                           // 만료 시간
        .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 서명
        .compact();
}
</code></pre>
  <ul>
    <li><b>Subject</b>: 토큰의 주체(사용자 ID)를 저장</li>
    <li><b>Claims</b>: 추가 정보(권한)를 key-value로 저장</li>
    <li><b>Expiration</b>: 토큰 만료 시간 설정으로 보안 강화</li>
    <li><b>Signature</b>: HMAC-SHA256으로 서명하여 위변조 방지</li>
  </ul>

  <h4>토큰 검증 (validateToken)</h4>
  <pre><code>java
public boolean validateToken(String token) {
    try {
        Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token);
        return true;
    } catch (ExpiredJwtException e) {
        log.error("토큰 만료: {}", e.getMessage());
    } catch (MalformedJwtException e) {
        log.error("잘못된 토큰: {}", e.getMessage());
    } catch (SignatureException e) {
        log.error("서명 검증 실패: {}", e.getMessage());
    }
    return false;
}
</code></pre>
  <ul>
    <li><b>만료 검증</b>: ExpiredJwtException 처리</li>
    <li><b>형식 검증</b>: MalformedJwtException 처리</li>
    <li><b>서명 검증</b>: SignatureException 처리</li>
  </ul>

  <!-- Authorization Filter 상세 -->
  <h3 id="full-filter">B. JwtAuthorizationFilter 상세</h3>
  <pre><code>java
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Authorization 헤더 추출
        String bearer = request.getHeader("Authorization");
        
        // 2. Bearer 토큰 형식 확인
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            String token = bearer.substring(7);
            
            // 3. 토큰 유효성 검증
            if (jwtTokenProvider.validateToken(token)) {
                int userId = jwtTokenProvider.getUserId(token);
                
                // 4. DB에서 사용자 조회
                User user = userRepository.findById(userId).orElse(null);
                
                if (user != null) {
                    // 5. CustomUserDetails 생성
                    CustomUserDetails userDetails = new CustomUserDetails(user);
                    
                    // 6. Authentication 객체 생성
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,                 // principal
                            null,                        // credentials
                            userDetails.getAuthorities() // authorities
                        );
                    
                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // 7. SecurityContext에 인증 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        
        // 8. 다음 필터로 진행
        filterChain.doFilter(request, response);
    }
}
</code></pre>

  <h4>필터 동작 원리</h4>
  <ul>
    <li><b>OncePerRequestFilter</b>: 요청당 한 번만 실행되도록 보장</li>
    <li><b>토큰 추출</b>: "Bearer " 접두사 제거</li>
    <li><b>검증 후 인증</b>: 유효한 토큰이면 SecurityContext에 저장</li>
    <li><b>필터 체인</b>: 인증 실패해도 다음 필터로 진행 (권한 체크는 별도)</li>
  </ul>

  <!-- Custom UserDetails -->
  <h3 id="full-userdetails">C. CustomUserDetails 구현</h3>
  <pre><code>java
@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    
    private final User user;
    
    @Override
    public Collection&lt;? extends GrantedAuthority&gt; getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public String getUsername() {
        return user.getLoginId();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    // 편의 메서드
    public int getUserId() {
        return user.getUserId();
    }
}
</code></pre>

  <!-- DTO 구조 -->
  <h3 id="full-dto">D. DTO 구조</h3>
  
  <h4>LoginRequestDto</h4>
  <pre><code>java
@Getter
@NoArgsConstructor
public class LoginRequestDto {
    
    @NotBlank(message = "아이디를 입력해주세요")
    private String loginId;
    
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
</code></pre>

  <h4>TokenResponseDto</h4>
  <pre><code>java
@Getter
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String tokenType;    // "Bearer"
    private String loginId;
    private String role;         // "USER" or "ADMIN"
}
</code></pre>

  <h4>UserSaveRequestDto (회원가입)</h4>
  <pre><code>java
@Getter
@NoArgsConstructor
public class UserSaveRequestDto {
    
    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 4, max = 20, message = "아이디는 4-20자여야 합니다")
    private String loginId;
    
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;
    
    @NotBlank(message = "이름을 입력해주세요")
    private String userName;
    
    @NotBlank(message = "전화번호를 입력해주세요")
    @Pattern(regexp = "^01[0-9]-[0-9]{4}-[0-9]{4}$", 
             message = "전화번호 형식이 올바르지 않습니다")
    private String userPhone;
}
</code></pre>

  <!-- Frontend Integration -->
  <h3 id="full-frontend">E. 프론트엔드 연동</h3>
  
  <h4>Axios 인스턴스 생성</h4>
  <pre><code>javascript
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});
</code></pre>

  <h4>요청 인터셉터 (토큰 자동 추가)</h4>
  <pre><code>javascript
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('✅ Authorization 헤더 추가:', config.headers.Authorization);
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
</code></pre>

  <h4>응답 인터셉터 (401 에러 처리)</h4>
  <pre><code>javascript
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 인증 실패 시 자동 로그아웃
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
</code></pre>

  <h4>로그인 API 호출</h4>
  <pre><code>javascript
export const authAPI = {
  login: (loginId, password) =>
    apiClient.post('/User/login', { loginId, password }),
  
  signup: (userData) =>
    apiClient.post('/User/signup', userData),
  
  getMyInfo: () =>
    apiClient.get('/User/myFindOne'),
};
</code></pre>

  <h4>로그인 페이지 구현</h4>
  <pre><code>javascript
const handleSubmit = async (e) => {
  e.preventDefault();
  
  try {
    const response = await authAPI.login(formData.loginId, formData.password);
    
    // 토큰 저장
    localStorage.setItem('token', response.data.accessToken);
    
    // 사용자 정보 저장
    const user = {
      loginId: response.data.loginId,
      role: response.data.role
    };
    localStorage.setItem('user', JSON.stringify(user));
    
    // 메인 페이지로 이동
    navigate('/');
    window.location.reload();
  } catch (error) {
    setError('아이디 또는 비밀번호가 일치하지 않습니다.');
  }
};
</code></pre>

  <!-- Security Config -->
  <h3 id="full-security-config">F. Spring Security 설정</h3>
  <pre><code>java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/User/login", "/User/signup").permitAll()
                .requestMatchers("/Admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthorizationFilter, 
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
</code></pre>

  <p><a href="#top">⬆ Back to top</a></p>
</details>

<hr/>

<!-- Insights -->
<h2 id="insights">11) 🌱 느낀점 &amp; 설계 의도</h2>
<details>
  <summary><b>클릭하여 펼치기</b></summary>
  <br/>

  <h3>🔐 보안의 중요성을 체감</h3>
  <p>
    인증 시스템은 단순히 "로그인 기능"이 아니라 <b>전체 애플리케이션의 보안 기반</b>이라는 것을 깨달았습니다.
    JWT 토큰 하나에도 만료 시간, 서명 알고리즘, Claims 설계 등 고려할 사항이 많았고,
    한 번의 실수가 전체 시스템의 보안 취약점이 될 수 있다는 점을 배웠습니다.
  </p>

  <h3>⚡ Stateless의 장점과 단점</h3>
  <p>
    JWT 기반 인증의 가장 큰 장점은 <b>서버 확장성</b>이었습니다. 세션을 사용하지 않으므로
    로드 밸런서 뒤에 여러 서버를 두어도 별도의 세션 공유 메커니즘이 필요 없었습니다.
    하지만 토큰이 탈취되면 만료 전까지 막을 방법이 없다는 단점도 있어,
    <b>Refresh Token 도입의 필요성</b>을 느꼈습니다.
  </p>

  <h3>🔧 Spring Security 필터 체인의 이해</h3>
  <p>
    처음에는 Spring Security의 복잡한 필터 체인이 어려웠지만,
    직접 <code>OncePerRequestFilter</code>를 구현하면서 각 필터의 역할과 순서를 이해하게 되었습니다.
    특히 <code>SecurityContextHolder</code>에 인증 정보를 저장하는 과정을 통해
    <b>인증(Authentication)과 인가(Authorization)의 분리</b>가 왜 중요한지 알게 되었습니다.
  </p>

  <h3>🤝 프론트엔드와의 협업</h3>
  <p>
    Axios Interceptor를 활용한 토큰 자동 관리는 프론트엔드 개발자와의 긴밀한 협업을 통해 완성했습니다.
    처음에는 매 요청마다 수동으로 헤더를 설정했지만, Interceptor 패턴을 도입하면서
    코드가 훨씬 깔끔해지고 유지보수성이 높아졌습니다.
    <b>백엔드와 프론트엔드의 역할 분리</b>가 얼마나 중요한지 실감했습니다.
  </p>

  <h3>📈 향후 개선 계획</h3>
  <ul>
    <li><b>Refresh Token 도입</b>: Access Token의 짧은 만료 시간과 Refresh Token을 조합하여 보안 강화</li>
    <li><b>Redis 캐싱</b>: 블랙리스트 관리 및 토큰 검증 성능 최적화</li>
    <li><b>OAuth2 연동</b>: 구글/카카오 소셜 로그인으로 사용자 편의성 향상</li>
    <li><b>로그 시스템</b>: 로그인 시도, 실패, 토큰 검증 실패 등을 기록하여 보안 모니터링</li>
  </ul>

  <p><a href="#top">⬆ Back to top</a></p>
</details>

<hr/>

<!-- Credits -->
<h2 id="credits">12) 👏 크레딧 & 참고 자료</h2>
<p>
  <b>개발자</b>: [이름]<br/>
  <b>Email</b>: [이메일]<br/>
  <b>GitHub</b>: <a href="[GitHub 프로필]" target="_blank" rel="noreferrer">[GitHub 프로필]</a><br/>
  <b>Blog</b>: <a href="[블로그]" target="_blank" rel="noreferrer">[블로그]</a>
</p>

<h3>참고 자료</h3>
<ul>
  <li><a href="https://docs.spring.io/spring-security/reference/" target="_blank">Spring Security 공식 문서</a></li>
  <li><a href="https://jwt.io/" target="_blank">JWT.io - JWT 디버거</a></li>
  <li><a href="https://github.com/jwtk/jjwt" target="_blank">JJWT - Java JWT 라이브러리</a></li>
</ul>

<p><a href="#top">⬆ Back to top</a></p>

<hr/>

<p align="center">
  <i>이 프로젝트는 팀 프로젝트의 일부로, 인증/보안 파트를 담당하여 개발했습니다.</i><br/>
  <i>Made with ❤️ and ☕</i>
</p>
