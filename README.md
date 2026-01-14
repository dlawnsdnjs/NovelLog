<h1> Novel Log</h1>

<p><strong>Novel Log는 판타지 소설 작가가 등장인물·장비·회차 정보를 체계적으로 관리할 수 있도록 돕는 세계관 관리 도구입니다.</strong></p>

<h2>📌 소개</h2>

<p>
판타지 소설에서는 등장인물의 성장이나 상태 변화를 표현하기 위해 <strong>상태창(Status Window)</strong>을 사용하는 경우가 많습니다.<br>
하지만 연재가 길어질수록 상태창 갱신 시 설정 누락이 발생하기 쉬우며, 매 회차마다 상태창을 반복해서 등장시키는 것도 현실적으로 어렵습니다.<br>
<br>
Novel Log는 이러한 문제를 해결하기 위해 다음 기능을 제공합니다:
</p>

<ul>
  <li>등장인물 목록 관리</li>
  <li>장비 목록 관리</li>
  <li>회차별 정보 관리</li>
</ul>

<p>작가가 설정을 놓치지 않도록 돕고, 변화 기록을 쉽게 추적할 수 있도록 하는 도구입니다.</p>

<hr>

<h2>🔗 데모 사이트</h2>
<p>
👉 <a href="https://novel-log.com/">https://novel-log.com/</a>
</p>

<hr>

<h2>🛠 기술 스택</h2>

<h3>Frontend</h3>
<ul>
  <li>React</li>
  <li>Tailwind CSS</li>
</ul>

<h3>Backend</h3>
<ul>
  <li>Spring Boot (MVC 패턴)</li>
  <li>Spring Security (JWT 인증)</li>
  <li>OAuth2 로그인 (Google, Naver)</li>
  <li>MyBatis (DB 매핑)</li>
</ul>

<h3>Database</h3>
<ul>
  <li>MySQL (주요 데이터)</li>
  <li>Redis (세션/캐시 데이터)</li>
</ul>

<hr>

<h2>🚀 배포 구조</h2>

<h3>Frontend (React)</h3>
<ul>
  <li>빌드 결과물을 Spring Boot 리소스에 포함해 통합 빌드</li>
  <li>개인 프로젝트 규모에서는 부담 없는 방식</li>
</ul>

<h3>Backend (Spring Boot)</h3>
<ul>
  <li>AWS EC2에서 서버 실행</li>
  <li>CloudFront를 통해 HTTPS 제공</li>
</ul>

<h3>Database</h3>
<ul>
  <li>MySQL: AWS RDS 사용</li>
  <li>Redis: ElasticCache → 비용 절감 위해 EC2 내부 설치로 변경</li>
</ul>

<hr>

<h2>📂 주요 기능 요약</h2>
<ul>
  <li>등장인물 관리: 정보·상태·회차별 변화 기록</li>
  <li>장비 관리: 장비 목록 및 인물 연결 관리</li>
  <li>회차 관리: 회차별 상세 정보 구조화</li>
  <li>JWT 인증: 쿠키로 1차 발급 → 프론트에서 헤더 사용</li>
  <li>OAuth2 로그인 지원: Google / Naver</li>
</ul>
