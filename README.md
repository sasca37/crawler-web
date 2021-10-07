## 크롤링 대행 사이트
- http://ec2-52-78-158-80.ap-northeast-2.compute.amazonaws.com/


![image](https://user-images.githubusercontent.com/81945553/135823421-8eadfb02-9a49-470d-8a1f-aef56b56e690.png)

- 요구사항에 맞게 크롤링 데이터를 제공(판매)하는 웹 사이트
- AWS 프리티어 사용 (운영 서버 2022년 9월에 종료 예정)



### 기술 스택

- Java 1.8
- Spring Boot 2.4.x 
  - Spring MVC, Spring Data JPA, Spring Security ... 
- AWS
  - EC2, S3, RDS, CodeDeploy 
- DataBase
  - Maria DB
- Junit5
- Gradle
- Git 
- Nginx 



### 기능

- Spring Security & Oauth2 로그인 (Google, Naver)
- 고객센터 REST API 
- AWS를 활용한 무중단 서버 배포 (Travis CI, CodeDeploy, Nginx)
- 고객센터  등급별 처리 (관리자 / 사용자) - 진행 중  



### 프로젝트 구조

![image](https://user-images.githubusercontent.com/81945553/134514182-2453cba6-90c2-46cb-8c1d-e8262ab3558b.png)

- 로컬 서버 (Window) 개발 - GitHub Push - Travis CI (GitHub 변경사항 감지) 
- S3 (jar 전달 받아 CodeDeploy 이동) - CodeDeploy에 보관 기능이 없다. 
- CodeDeploy - CI로 받은 배포 요청과 S3에게 받은 jar를 통해 EC2에 배포 
- EC2에선 Nginx를 통해 무중단 배포 
- 배포 자동화 과정 : https://sasca37.tistory.com/category/SPRING/AWS



### 프로젝트 기간

- 2021.09.20 ~ 진행 중 



### TODO_LIST 

- 외부 사용자 로그인 허용 
  - 도메인 구입(Route53)을 통한 https 적용 - 구글 로그인 적용 (현재 EC2 도메인으로 SSL/TLS 적용 불가)
  - 네이버 Oauth 로그인을 허용 요청 - 네이버에 요청 필요

- 고객센터 페이지 - 관리자, 사용자 기능 추가 

- 라이브 채팅 기능  
