## 크롤링 대행 사이트
- http://ec2-52-78-158-80.ap-northeast-2.compute.amazonaws.com/


![image](https://user-images.githubusercontent.com/81945553/135823421-8eadfb02-9a49-470d-8a1f-aef56b56e690.png)

- 요구사항에 맞게 크롤링 데이터를 제공(판매)하는 웹 사이트
- AWS 프리티어 사용 (운영 서버 2022년 9월에 종료 예정)



### 기술 스택

- Java 1.8
- Spring Boot 2.5.x 
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
- 고객센터 관리자 페이지 - 진행 중  
- 고객센터 댓글기능 Disqus 사용


### 프로젝트 구조

![image](https://user-images.githubusercontent.com/81945553/134514182-2453cba6-90c2-46cb-8c1d-e8262ab3558b.png)

- 로컬 서버 (Window) 개발 - GitHub Push - Travis CI (GitHub 변경사항 감지) 
- S3 (jar 전달 받아 CodeDeploy 이동) - CodeDeploy에 보관 기능이 없다. 
- CodeDeploy - CI로 받은 배포 요청과 S3에게 받은 jar를 통해 EC2에 배포 
- EC2에선 Nginx를 통해 무중단 배포 
- 배포 자동화 과정 : https://sasca37.tistory.com/category/SPRING/AWS



### 프로젝트 기간
- 1인 개발 (2021.09.20 ~ 10.01)



### TODO_LIST 

- 외부 사용자 로그인 허용 
  - 도메인 구입(Route53)을 통한 https 적용 - 구글 로그인 적용 (현재 EC2 도메인으로 SSL/TLS 적용 불가)
  - 네이버 Oauth 로그인을 허용 요청 - 네이버에 요청 필요

- 고객센터 페이지 - 관리자, 사용자 기능 추가 

- 라이브 채팅 기능  
- Travis CI 크레딧 구매
- 크롤링 데이터의 정확성을 위한 데이터 전처리, SVM, randomForest, glm 등 정확성 높은 모델 평가 



### 프로젝트를 하며 얻은 점 

#### JPA를 사용하는 이유

- 객체지향적인 프로그래밍에서 관계형 데이터베이스를 이용하게 되면 객체 모델링 보다는 테이블 모델링에 집중하게 된다. 즉,   객체가 아닌 테이블을 위한 모델링으로 만들어서 사용하게된다. 예를 들어 RDBMS에서는 외래키와 join 연산을 통해서 테이블 간의 데이터를 가져올 수 있지만, 객체의 상속관계를 감안했을 때 패러다임이 불일치하다고 볼 수 있기 때문이다. 
- SpringDataJPA는 JPA를 더 쉽게 추상화시킨 모듈로써 Hibernate를 쓰는 것과 비슷하다고 볼 수 있다. Hibernate보다 SpringDataJPA를 쓰는 것이 좋은 이유는 구현체 교체의 용이성, 저장소 교체의 용이성 등이 있다. 즉 SpringDataJPA를 사용하다가 SpringDataRedis로 변경하는 것과 같이 쉽게 교체가 가능하기 때문이다. 
- JPA를 사용하기 위해선 객체지향프로그래밍과 관계형 데이터베이스를 모두 이해해야하기 때문에 높은 난이돌르 갖고있다. 하지만 사용할줄만 안다면 CRUD를 직접 작성하지 않아도 네이티브 쿼리만큼의 퍼포먼스를 보여줄 수 있다.

#### JPA 어노테이션

- @Entity : 테이블과 링크 될 클래스임을 나타내는 어노테이션 (기본적으로 카멜케이스를 언더스코어 네이밍으로 생성)
- @GeneratedValue : PK 생성 규칙
  - GeneratedValue(strategy = GenerationType.IDENTITY) : 오토 인크리먼트 방식 (기본 키 생성을 데이터베이스에 위임)
  - strategy=GenerationType.SEQUENCE : 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트 
  - 
- @Column : 테이블의 컬럼을 나타내며 컬럼에 대한 추가 옵션이 있을 때 사용 



#### JPA Repository Interface

```java
public interface PostsRepository extends JpaRepository<Posts, Long> {}
```

- Repository : MyBatis 등에서 Dao라고 불리는 DB 접근 계층을 대신하는 인터페이스 

- postsRepository.save(Posts) : 테이블에 데이터가 있다면 update, 없다면 insert 
- postsRepository.findAll() : 모든 데이터 리스트 반환
- postsRepository.deleteAll() : 모든 데이터 삭제 



#### JPA REST API 예시

 ```java
 @RequiredArgsConstructor
 @RestController
 public class PostsApiController {
   private final PostsService postsService;
   
   @PostMapping("/api/v1/posts")
   public Long save(@requestBody PostsSaveRequestDto requestDto) {
     return postsService.save(requestDto)
   }
 }
 ```

- 컨트롤러에서 데이터를 인자에 할당하는 방법 (@RequestBody, @RequestParam)
  - @RequestBody  : 값 전체를 String으로 받아옴 
  - @RequestParam : 변수에 저장한 변수명과 쿼리스트링 데이터명이 일치하면 받아옴 (form 태그 등 url 상에서 데이터를 전달하는 경우 사용) 



```java
@RequiredArgsConstructor
@Service
public class PostsService {
  private final PostsRepository postsRepository;
  @Transactional
  public Long save(PostsSaveRequestDto requestDto) {
    return postsRepository.save(requestDto.toEntity()).getId();
  }
}
```



#### 영속성 컨텍스트 

- 영속성 컨텍스트란 엔티티를 영구 저장하는 환경 
- JPA의 핵심 내용은 엔티티가 영속성 컨텍스트에 포함되어 있느냐 아니냐로 갈린다. 



### SSL과 TLS

SSL과 TLS는 네트워크를 통해 작동하는 인증 및 데이터 암호화를 제공하는 암호화 프로토콜 입니다. SSL은 대칭키와 공개키와 같은 암호화 기법으로 데이터를 암호화 하며, SSL 인증서를 통해 허가된 클라이언트에게만 데이터를 줄 수 있도록 만들어져 있습니다. SSL에서의 취약점을 개선하여 표준화한 것이 TLS 방식입니다.

- 대칭 키 : 키 값을 가지고 데이터를 복호화 하는 기법
- 공개 키 : 대칭 키의 키값을 해킹당할 경우 무용지물이 되므로 공개키와 비공개 키를 갖습니다. 공개 키는 암호화는 가능하지만 복호화를 위해선 비공개키를 사용해야만 복호화가 가능하도록 설계되어있습니다. (공개 키를 가진 사람의 신원을 보장해주는 역할도 한다 이 의미를 전자 서명이라고 부른다.)



### 스프링 시큐리티와 소셜 로그인

스프링 프레임워크에는 시큐리티라는 인증과 인가처리를 해주는 기능이 있습니다. 구글이나 네이버같은 소셜에서 API 키와 URI를 발급받아 처리하는 과정을 공부했습니다. 스프링 시큐리티에서는 권한 코드에 항상 “ROLE_”가 들어가야한다는 점과 회원을 관리하기 위한 객체를 저장하는 과정에서 직렬화 문제를 겪어봤습니다. 직렬화 문제가 발생된 원인은 동시에 오는 요청에 처리에대한 신뢰성을 보장할 수 없기 떄문이었습니다. 그렇기 때문에 직렬화 처리를 하여 요청이 끝나면 다음 요청을 받는 식으로 처리하여 해결하였습니다.

- user에 대한 세션은 httpSession.setAttribute(”user”, new SessionUser(user)로 받아옴 (SessionUser는 User 객체를 직렬화 처리를한 dto 클래스)
- 세션은 WAS의 메모리에 저장되어있기 때문에 서버가 종료되면 자동으로 세션도 사라지는 경우가 있었습니다. 이를 해결하기위해 세션 저장소로 데이터베이스를 선택하면서 spring.session.store-type=jdbc로 세션 저장소 프로퍼티를 설정하였습니다. (RDS를 사용하여 해결)
- 인증 : 유저가 누구인지 확인하는 절차 (회원가입 및 로그인)
- 인가 : 유저에 대한 권한을 허락하는 것



#### 클라우드

클라우드란 가상화된 서버에 프로그램을 두고 사용하는 방식을 의미합니다. 이러한 클라우드는 빠른 인프라도입, 오토스케일과 같은 트래픽 대응, 강력한 보안과 서비스, 가변적인 금액 등의 장점이 있습니다. 한편으로는 이용하는 서비스가 많아질 수록 비용측면에서 부담이 될 수 있고, 혹시나 생길 수 있는 문제에 책임을 묻기 어렵다는 단점도 존재합니다.

클라우드는 크게 서비스모델과 디플로이먼트 모델 2가지로 나누어볼 수 있습니다. 서비스 모델에는 Iaas Paas Saas 3가지가 존재하며, 디폴리이먼트 모델은 퍼블릭 , 프라이빗, 하이브리드 클라우드 등이 존재합니다. IaaS  :  IT 기본 자원만 제공 (Server, Storage, Network) - 새로 컴퓨터 하나 구매하는 느낌 (OS 설치되지 않는다.)

PaaS : IaaS에 OS, Middleware, Runtime 추가 - 일반적으로 개발자를 대상으로 함 (코드만 개발해서 올리면 서비스 제공할 수 있는 환경 - Beanstalk, App Engine 등)

SaaS : 소프트웨어 자체를 제공 받아서 사용받는다. (구글 드라이브, 네이버 클라우드 등)

Public Cloud : 외부에 클라우드가 존재하고 그 클라우드를 모든 사용자들이 사용 가능 - 일반적으로 사용하는 방식

Private Cloud : 기업 내부에서만 사용가능한 환경 - 구축 난이도는 높지만, 보안이난 법적 규제 등의 이유로 사용

Hybrid Cloud : Public / Private를 조합해서 사용하는 형태 , 보안이 중요하면 Private 그 외 시스템은 Public 사용하거나 private을 쓰면서 트래픽이 몰리는 구간을 public으로 사용



#### AWS

- EC2

  AWS에서 제공하는 성능, 용량 등을 유동적으로 사용할 수 있는 서버

  프리티어 플랜에서는 t2.micro 사양 만 사용 (월 750시간 제한)

  AMI (Amazone Machine Image) : 아마존 리눅스 2와 같은 인스턴스 설치

  기업에서 사용할 경우 VPC, 서브넷 등 추가 관리필요 (여러 ec2를 설치하고 관리하기 위해서)

  윈도우 환경에서는 putty를 통해 pem키를 apk로 만들어서 넣은 후 EIP와 22포트로 접속

- RDS

  직접 데이터베이스를 설치하지 않고 AWS에서 데이터베이스를 관리해주는 관리형 서비스

  로컬 IDE 환경에서 접속할 때 Database 플러그인을 설치하여 EIP 3306포트로 접속

- 배포

  작성한 코드를 실제 서버에 반영하는 행위를 의미

  EC2 환경에 깃 클론을 하고 쉘 스크립트(.sh)를 통해 배포 과정을 자동화 한다.

  쉘스크립트 내용으론 깃 풀 받고 프로젝트의 .jar의 위치 nohup으로 실행 (단, 로컬 환경에서 git ignore을 했다면, ec2환경에서도 추가적으로 파일을 생성해줘야한다.)

  - Travis CI 배포 자동화

    Travis CI 는 깃허브에서 제공하는 무료 CI 서비스 (젠킨스와 같은 CI 도구도 존재, 단 설치형이기 때문에 별도의 ec2 인스턴스가 필요)

    Travis CI 는 yml(야믈) 파일 확장자를 사용 - JSON에서 괄호를 뺀 형태

    즉, Travis CI에 계정을 연동하고 프로젝트 gradle 에 yml 파일을 생성하면 연결 완료.

    동작 과정은 Travis CI가 jar 파일을 만들어서 s3에 보내고 codeDeploy에 배포 요청을 한다. s3에 보내는 이유는 codeDeploy에 저장 기능이 없기 때문에 별도의 파일 저장 시스템이 필요하기 때문이다. codeDeploy는 s3를 통해 jar 파일을 전달받고 travisCI에게서 받은 배포요청으로 ec2 인스턴스에 전달한다.

    - Travis CI가 S3(버킷)와 CodeDeploy에 접속하기 위해선 AWS 보안 설정 (IAM) 키를 생성하여 적용해줘야한다.
    - CI : Continous Integration (지속적인 통합) : 코드 버전 관리를 하는 VCS 시스템이 PUSH가 되면 자동으로 빌드가 수행되어 배포 파일을 만드는 과정
    - CD : Continous Deployment 배포 파일을 자동으로 운영서버에 배포해주는 과정

  - 무중단 배포 (Nginx)

    기존에는 배포는 큰 이벤트기 때문에 개발자들이 직접 사용자가 적은 새벽시간에 배포하고 문제가 발생 시 긴급 점검을 올리고 해결하는 방식을 이용해왔다. 이러한 불편함을 해결하기 위해 도커, L4, 블루 그린, 엔진엑스 등을 이용하여 무중단 배포를 사용할 수 있다.

    엔진엑스 1대에 애플리케이션 배포버전을 2개를 두어 한 개는 서버를 돌리는 용도고 한 개는 다음 배포용으로 만들어 둔다. (reload는 0.1초 만에 이루어지기 때문에 사용자에게 무중단 배포처럼 보임)

    - Nginx의 리버스 프록시 기능 : 엔진엑스가 외부의 요청을 받아 백엔드 서버로 요청을 전달하는 것

