<div align="center">
  <h1>🐦 Twitter Clone</h1>
  <p><strong>RDBMS 스키마 설계 기반 트위터 클론 프로젝트</strong></p>
  <p>
    <img src="https://img.shields.io/badge/Java%2021-007396?style=flat-square&logo=openjdk&logoColor=white"/>
    <img src="https://img.shields.io/badge/JDBC-007396?style=flat-square&logo=openjdk&logoColor=white"/>
    <img src="https://img.shields.io/badge/MySQL%209.0-4479A1?style=flat-square&logo=mysql&logoColor=white"/>
    <img src="https://img.shields.io/badge/Node.js-339933?style=flat-square&logo=nodedotjs&logoColor=white"/>
    <img src="https://img.shields.io/badge/Eclipse-2C2255?style=flat-square&logo=eclipseide&logoColor=white"/>
  </p>
  <p>
    <img src="https://img.shields.io/badge/과목-관계형_데이터베이스_및_실습-blue?style=flat-square"/>
    <img src="https://img.shields.io/badge/유형-대학교_팀_프로젝트-lightgrey?style=flat-square"/>
    <img src="https://img.shields.io/badge/배포_상태-미배포-lightgrey?style=flat-square"/>
  </p>
</div>

---

## 📌 목차

1. [프로젝트 소개](#-프로젝트-소개)
2. [기술 스택](#-기술-스택)
3. [주요 기능](#-주요-기능)
4. [DB 스키마](#️-db-스키마)
5. [프로젝트 구조](#-프로젝트-구조)
6. [시작하기](#-시작하기)

---

## 📋 프로젝트 소개

이 프로젝트는 **'관계형 데이터베이스 및 실습'** 과목의 최종 프로젝트로 진행된 트위터 클론입니다.

단순한 기능 구현을 넘어 **RDBMS 스키마 설계와 성능 최적화**가 핵심 평가 요소였습니다. Spring과 같은 프레임워크 없이 **순수 Java + JDBC로 SQL을 직접 작성**하여 DB와 통신하며, 소셜 네트워크 서비스의 주요 기능을 구현했습니다.

> 팔로우 피드, 해시태그 검색, 좋아요/북마크, 댓글·대댓글, 활동 기반 인플루언서 레벨 시스템 등 트위터의 핵심 기능을 직접 설계한 DB 스키마 위에서 동작하도록 구현했습니다.

---

## 🛠 기술 스택

### Backend

| 기술 | 버전 | 비고 |
| :--- | :---: | :--- |
| Java | 21 | 메인 언어 |
| JDBC | - | 순수 JDBC로 SQL 직접 작성 (ORM 미사용) |
| MySQL Connector/J | 9.0.0 | MySQL JDBC 드라이버 |
| Eclipse IDE | - | 개발 환경 (`.project` / `.classpath` 기반) |

### Frontend

| 기술 | 비고 |
| :--- | :--- |
| Node.js | 프론트엔드 서버 |

### Database

| 기술 | 비고 |
| :--- | :--- |
| MySQL | RDBMS 설계 중심, 정규화 및 인덱스 최적화 |

---

## ✨ 주요 기능

### 👤 사용자 (User)

- SNS 계정 기반 로그인
- 프로필 조회 및 관리
- **활동 이력 기반 레벨 시스템** — 게시글 수, 팔로워 수 등 활동 이력을 집계하여 인플루언서 여부를 자동으로 판별

### 🤝 팔로우 (Follow)

- 다른 사용자 팔로우 / 언팔로우
- **팔로우한 사용자의 게시글만 피드에 노출** — 팔로잉 기반 타임라인 구성

### 📝 게시글 (Tweet)

- 게시글 작성 · 수정 · 삭제
- 해시태그 첨부 기능
- 게시글 좋아요 / 좋아요 취소
- 게시글 북마크 / 북마크 취소
- **해시태그 기반 키워드 검색**

### 💬 댓글 · 대댓글 (Comment)

- 다른 사용자의 게시글에 댓글 작성 · 수정 · 삭제
- 댓글에 대댓글 작성 · 수정 · 삭제

---

## 🗄️ DB 스키마

<details>
<summary><b>ERD (Entity Relationship Diagram) 보기</b></summary>

<br/>

> DB 설계가 이 프로젝트의 핵심 평가 요소입니다.
> 사용자, 게시글, 댓글, 팔로우, 해시태그, 좋아요, 북마크 등의 관계를 정규화된 형태로 설계했습니다.

![DB Schema](https://github.com/user-attachments/assets/ecbab553-d2bf-49c6-8d68-51ab9b190b49)

</details>

---

## 📂 프로젝트 구조

```
Twitter/
├── src/
│   └── twitterProj/          # 메인 패키지
│       ├── db/               # JDBC 연결 및 SQL 쿼리 처리
│       ├── model/            # 도메인 모델 (User, Tweet, Comment, Hashtag 등)
│       ├── dao/              # DB 접근 객체 (DAO 패턴)
│       └── service/          # 비즈니스 로직
├── bin/
│   └── twitterProj/          # 컴파일된 .class 파일
├── .classpath                # Eclipse 클래스패스 설정 (Java 21, MySQL Connector 참조)
├── .project                  # Eclipse 프로젝트 설정
└── README.md
```

> 프론트엔드(Node.js)는 별도 레포지토리 또는 디렉토리로 관리됩니다.

---

## 🚀 시작하기

### 사전 요구사항

- Java 21+
- MySQL 서버
- MySQL Connector/J 9.0.0 (`mysql-connector-j-9.0.0.jar`)
- Eclipse IDE (권장) 또는 javac 직접 실행
- Node.js (프론트엔드 실행 시)

### 실행 순서

**1. 레포지토리 클론**

```bash
git clone https://github.com/CoderGogh/Twitter.git
```

**2. MySQL 데이터베이스 및 스키마 설정**

```sql
CREATE DATABASE twitter_clone;
USE twitter_clone;
-- ERD를 참고하여 테이블 생성 스크립트 실행
```

**3. DB 연결 정보 설정**

`src/twitterProj/db/` 경로의 DB 연결 설정 파일에서 아래 항목을 입력합니다.

```java
String url      = "jdbc:mysql://localhost:3306/twitter_clone";
String user     = "{DB_USER}";
String password = "{DB_PASSWORD}";
```

**4. MySQL Connector/J 경로 설정 (Eclipse 사용 시)**

`.classpath` 파일의 드라이버 경로를 로컬 환경에 맞게 수정합니다.

```xml
<!-- 변경 전 (원본) -->
<classpathentry kind="lib" path="C:/Users/hetmi/Downloads/mysql-connector-j-9.0.0.jar"/>

<!-- 변경 후 (본인 환경에 맞게 수정) -->
<classpathentry kind="lib" path="{본인의 mysql-connector-j-9.0.0.jar 경로}"/>
```

**5. 백엔드 실행**

Eclipse에서 프로젝트를 import한 후 메인 클래스를 실행하거나, 터미널에서 직접 컴파일·실행합니다.

```bash
# 컴파일
javac -cp ".;mysql-connector-j-9.0.0.jar" src/twitterProj/**/*.java -d bin

# 실행
java -cp "bin;mysql-connector-j-9.0.0.jar" twitterProj.Main
```

**6. 프론트엔드 실행 (Node.js)**

```bash
# 프론트엔드 디렉토리 이동 후
npm install
npm start
```

---

<div align="center">
  <sub>대학교 '관계형 데이터베이스 및 실습' 과목 최종 프로젝트</sub>
</div>
