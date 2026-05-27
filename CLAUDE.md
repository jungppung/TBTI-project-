# ⚙️ TBTI 백엔드 개발 지침 및 아키텍처 (CLAUDE.md)

이 문서는 Claude Code(claude.ai/code)가 이 저장소의 코드를 작업할 때 일관된 규칙을 따르도록 가이드라인을 제공합니다.

---

## 1. 프로젝트 개요 (Project Overview)
- **서비스명:** TBTI (Travel Based Type Indicator)
- **도메인:** 실시간 환율 및 물가 데이터를 기반으로 한 목적지별 여행 구매력 & 지갑 사정 진단 서비스
- **동작 방식:** 사용자가 원화 예산과 여행 일정을 입력하면, 백엔드에서 실시간 환율과 현지 물가 통계 상수를 결합하여 '실질 구매력'을 정량적으로 계산합니다. 계산된 결과는 단순 수치가 아닌 8가지 위트 있는 캐릭터 페르소나 유형(3글자 코드)으로 변환되어 제공되며, Bored API를 통해 지갑 사정에 맞는 맞춤형 활동 추천까지 연동합니다.

---

## 2. 기술 스택 (Tech Stack)
- **Java Version:** Java 17
- **Framework:** Spring Boot 3.x
- **Database:** MySQL (스키마명: `tbti_db`, 포트: 3306)
- **ORM:** Spring Data JPA (`ddl-auto: validate` 설정 필수)
- **Security:** Spring Security + JWT (토큰 유효시간 24시간)
- **Lombok:** 활성화 (`@Getter`, `@Setter`, `@NoArgsConstructor` 적극 사용)

---

## 3. 프로젝트 아키텍처 및 패키지 구조
모든 클래스는 `com.example.tbti` 패키지 하위의 지정된 폴더에 위치해야 합니다:
- `domain`: 데이터베이스 테이블과 1:1 매핑되는 JPA 엔티티 (User, UserHistory 등)
- `repository`: DB CRUD를 담당하는 Spring Data JPA 인터페이스
- `service`: 비즈니스 로직의 심장부 (구매력 계산 연산, 페르소나 분류, JWT 발급 등)
- `controller`: 안드로이드 클라이언트와 통신하는 REST 컨트롤러
- `dto`: 요청(Request) 및 응답(Response) 데이터 전달 객체 가방
- `security`: JWT 필터 및 Spring Security 설정 클래스
- `external`: 외부 API 통신 클라이언트 (환율 API, Bored API)

---

## 4. 백엔드 개발 핵심 규칙 (CRITICAL)
1. **비밀번호 암호화:** 회원가입 시 유저의 비밀번호는 반드시 `BCryptPasswordEncoder`를 사용하여 암호화한 후 DB에 저장해야 합니다. 절대 평문으로 저장하지 마십시오.
2. **소수점 정밀도:** 환율 연산, 물가 상수 계산, 원화 환산 등의 금전 계산 시 부동 소수점 오차를 방지하기 위해 반드시 `BigDecimal` 또는 정수형(`int`/`long`) 단위를 안전하게 사용하십시오.
3. **공백 규칙 (YAML):** `application.yml` 파일 작성 시 콜론(`:`) 뒤에 반드시 한 칸의 공백(띄어쓰기)을 유지해야 합니다.
4. **예외 처리:** 안드로이드 앱과의 안정적인 통신을 위해 `@RestControllerAdvice`를 이용한 글로벌 예외 처리를 구현하고, 정형화된 JSON 에러 포맷을 반환하십시오.

---

## 5. 핵심 기능별 세부 구현 스펙 (Feature Specifications)

### 🔐 기능 1: 회원 관리 및 인증 (User Management)
- **회원가입 (POST `/api/users/signup`)**
    - 입력 데이터: 로그인 아이디(`username`), 비밀번호(`password`), 닉네임(`nickname`)
    - 규칙: `username` 중복 시 예외 처리, 비밀번호는 `BCrypt` 암호화 필수.
- **로그인 (POST `/api/users/login`)**
    - 입력 데이터: 로그인 아이디(`username`), 비밀번호(`password`)
    - 결과물: 인증 성공 시 **JWT 토큰**을 발급하여 응답.

### 💱 기능 2: 실시간 환율 동기화 스케줄러 (Exchange Rate Scheduler)
- 매일 특정 시간에 외부 환율 API를 호출하여 최신 환율 데이터를 `exchange_rate` 테이블에 업데이트(`ON UPDATE CURRENT_TIMESTAMP`)하여 캐싱합니다.

### 💰 기능 3: TBTI 지갑 사정 및 여행 구매력 진단 (Core Logic)
- **진단 요청 (POST `/api/tbti/diagnose`)**
    - 입력 데이터: 유저 가용 예산(원화), 여행 기간(n일), 선택한 도시 ID, 소비 성향(VALUE/ALL_IN), 활동 성향(STATIC/DYNAMIC)
    - **연산 메커니즘:**
        1. 총 원화 예산을 선택한 도시의 '최신 환율'로 나누어 **현지 화폐 기준 총 예산**을 구합니다.
        2. 이를 여행 기간(n일)으로 나누어 **현지 화폐 기준 1일 예산**을 산출합니다.
        3. 이 값을 `city_cost` 테이블의 **1일 표준 생활비(상수)**와 비교하여 유저의 '실질 여행 구매력 지수'를 정량적으로 도출합니다.
    - **결과물:** 구매력 수준과 소비/활동 성향을 조합하여 **8가지 캐릭터 유형 코드(ex: HAD, LVS)**를 결정하고, `user_history`에 저장 후 반환합니다.

### 🗺️ 기능 4: 추천 활동 저장 및 북마크 (Saved Activity & Bored API)
- 도출된 구매력 클래스와 활동 성향에 맞는 카테고리를 기준으로 외부 Bored API를 필터링 호출하여 유저에게 보여주고, 유저가 찜한 활동은 `saved_activity` 테이블에 저장합니다.

---

## 6. Git Flow 개발 프로세스
우리는 기능별로 브랜치를 분리하여 작업하는 단순화된 **Git Flow** 전략을 따릅니다.

### 🌿 브랜치 전략
- `main`: 제품 출시 및 배포용 브랜치. 직접 커밋 금지.
- `develop`: 기능들을 합치는 메인 개발 브랜치. 모든 작업의 중심.
- `feature/기능명`: 새로운 기능을 개발하는 독립 브랜치 (예: `feature/user-signup`). 항상 `develop` 브랜치로부터 생성하며, 작업 완료 후 `develop`으로 Pull Request(PR)를 보냅니다.

---

## 7. 커밋 메시지 컨벤션 (Commit Message Convention)
모든 커밋 메시지는 통일감을 주기 위해 아래 규칙(`type: 내용`)을 엄격히 준수합니다.

- `feat`: 새로운 기능 추가 (예: `feat: 사용자 회원가입 및 BCrypt 암호화 구현`)
- `fix`: 버그 및 오류 수정 (예: `fix: yml 파일 한글 인코딩 깨짐 현상 수정`)
- `docs`: 문서 수정 (예: `docs: README.md 목표 및 타겟층 구체화`)
- `refactor`: 코드 리팩토링 (기능 변화 없이 코드 구조만 개선)
- `test`: 테스트 코드 추가 및 수정
- `chore`: 빌드 업무 수정, 패키지 구조 변경, 라이브러리 추가 등