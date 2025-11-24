# Novel Log
등장인물의 상태 관리를 Novel Log를 통해 간편하게 관리해 보세요!
<br>
판타지 소설에서 등장인물의 성장 등의 변화를 독자들에게 보여주기 위한 장치로 상태창을 사용하는 경우가 많습니다.
연재가 길어지게 되면 상태창을 오랜만에 갱신하면서 빠뜨리는 부분이 생기면서 설정 오류가 생기는 경우도 많아집니다.
상태창은 상태를 보기 쉽게 정리하지만 실제 연재에서는 변화가 생길 때마다 등장시키기엔 무리가 있습니다.
이를 보완하기 위해서 Novel Log에서는 ( 등장인물 목록 / 장비 목록 / 회차 목록 ) 기능을 이용해 설정 관리에 도움을 줍니다.
<br>
현재 Novel Log 주소:
https://d2dan5t0q4jwaf.cloudfront.net/
<br>
프로젝트 설명:
NovelLog/
프론트엔드     --- React 프로젝트, tailwind 모듈 사용
백엔드         --- Spring-Boot 프로젝트, MVC 패턴 사용, MyBatis로 DB 매핑 작업
                   Spring Security를 사용한 보안 관리(JWT 발급)
                   개인별 관리를 위해 로그인 기능 사용[일반 로그인 / OAuth2 로그인(Google, Naver)]
데이터베이스   --- 주요 데이터 MySQL 사용, 기타 데이터 Redis


배포 관련:
React 프로젝트    -  build 결과를 Spring-Boot 프로젝트에 포함시켜서 함께 build
                    대형 프로젝트가 아닌 개인 프로젝트로 진행해 통합해도 큰 영향 없음
Spring-Boot 서버  -  AWS EC2 INSTANCE로 서버 실행 및 Cloud Front로 배포하여 https 도메인 사용
MySQL - AWS RDS로 서버와 연결
Redis - Elastic Cache를 처음 연결했다가 비용 문제로 AWS EC2 INSTANCE에 설치하여 서버와 연결
