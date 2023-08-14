

1. H2 데이터베이스 다운로드
   - [h2 공식 사이트](https://www.h2database.com/html/download-archive.html)

2. `sh ./h2/bin/h2.sh` 접속
3. `http://218.38.137.28:8082/?key=` ➡️ `http://localhost:8082/?key=`
4. 최초 한번 파일 직접 접근 후 `~/test.mv.db` 파일 생성 확인
   - JDBC URL: `jdbc:h2:~/test`
   - 사용자명: `sa`
5. 그 이후는 프로토콜로 접속
   - JDBC URL: `jdbc:h2:tcp://localhost/~/test`
   - 사용자명: `sa`



### JDBC 이해
- JDBC 는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API
- 애플리케이션 로직은 JDBC 표준 인터페이스에만 의존 ➡️ **DB 교체 시 JDBC 드라이버만 변경하면 됨**
- 한계 : JDBC를 row 레벨로 사용 시 반복 코드가 많다
<details>
<summary>JDBC Connection 인터페이스</summary>
<div markdown="1">
  
- jdbc는 java.sql.Connection 표준 커넥션 인터페이스를 정의함
- H2 데이터베이스 드라이버는 JDBC Connection 인터페이스를 구현한 `org.h2.jdbc.jdbcConnection` 구현체를 제공
</div>
</details>

<details>
<summary>JDBC DriverManager </summary>
<div markdown="1">
  
- 애플리케이션 로직에서 커넥션이 필요하면 `DriverManager.getConnection()` 호출
- `DriverManager`는 라이브러리에 등록된 드라이버 목록을 자동으로 인식 
- 이 드라이버들에게 순차적으로 커넥션을 획득할 수 있는지 확인함
- 이렇게 찾은 커넥션 구현체가 클라이언트에게 반환됨
</div>
</details>

<details>
<summary>JDBC ResultSet </summary>
<div markdown="1">

- `ResultSet`은 아래와 같이 생긴 데이터 구조

   ![27B4443359753B7C04](https://github.com/thdefn/Deep-Dive-SpringDB/assets/80521474/a6f12b45-b1f4-4d0b-89ab-7a26092f6c17)

- `Cursor` : `ResultSet` 내부에 있는 커서를 이동해서 데이터를 조회
  - `rs.next()` 를 호출하면 커서가 이동하고, row 존재 여부를 반환
    - `rs.next()` 결과가 `true`면 커서의 이동 결과 데이터가 있다는 뜻
    - `rs.next()` 결과가 `false`면 커서의 이동 결과 데이터가 없다는 뜻
</div>
</details>


### 커넥션 풀과 데이터소스 이해
- DB 커넥션을 매번 획득할 경우 애플리케이션 로직마다 **커넥션을 새로 만드는 시간과 리소스가 소요**된다.
    - **커넥션** 획득 과정
      1. 애플리케이션 로직은 DB 드라이버를 통해 커넥션을 조회한다.
      2. DB 드라이버는 DB와 TCP/IP 커넥션을 연결한다.
      3. DB 드라이버는 ID, PW를 통해 DB에 인증하고 내부에 DB 세션을 생성한다.
      4. DB가 커넥션 생성이 완료되었다는 응답을 보낸다.
      5. DB 드라이버는 커넥션 객체를 생성해서 클라이언트에게 반환한다.
- 따라서 애플리케이션을 띄울 때 커넥션 객체를 미리 생성해두고 사용하는데, 이를 **커넥션 풀** 이라고 한다.
<details>
<summary>커넥션 풀 </summary>
<div markdown="1">

- **커넥션 풀**을 통한 커넥션 획득 과정
    1. 애플리케이션을 띄우는 시점에 커넥션 풀은 커넥션을 미리 확보해서 풀에 보관한다.
    2. 애플리케이션 로직이 커넥션 풀에 커넥션을 요청하면 커넥션은 자신이 가지고 있는 커넥션 중 하나를 반환한다.
    3. 애플리케이션 로직은 해당 커넥션을 사용해서 SQL을 DB에 전달하고 그 결과를 받는다. 
        - 커넥션 풀에 들어 있는 커넥션은 DB와 TCP/IP 커넥션이 연결되어 있는 상태이기 때문에 언제든지 즉시 SQL을 전달할 수 있다.
    4. 애플리케이션 로직이 커넥션을 모두 사용하면, 커넥션이 살아 있는 상태로 커넥션 풀에 해당 커넥션을 반환한다.
- 커넥션 풀을 이용하면 애플리케이션 로직에 DB와 커넥션을 맺는 시간이 소요되지 않는다.
- 커넥션 풀은 서버 당 최대 커넥션 수를 제한할 수 있어, DB를 보호한다.
- 애플리케이션을 띄울 때 커넥션 객체를 미리 생성해두고 사용하는 방법  
  <img src="https://github.com/thdefn/Deep-Dive-SpringDB/assets/80521474/45434bc9-b7b2-4348-97d0-f9ccb1789bd8" width="400" height="300"/>


</div>
</details>
<details>
<summary>데이터 소스 </summary>
<div markdown="1">

- 커넥션을 얻는 방법은 앞서 학습한 `JDBC DriverManager` 를 직접 사용하거나, 커넥션 풀을 사용할 수 있다. 
  - 만약 `JDBC DriverManager`를 통해 커넥션을 획득하다가, HikariCP 커넥션 풀을 사용하는 방법으로 변경한다면?
    
    ➡️ **커넥션을 획득하는 애플리케이션의 코드를 함께 변경**해야 한다. DriverManager 에서 HikariCP 로 의존 관계가 바뀌기 때문이다.
  - 뿐만 아니라 DPCP2 에서 HikariCP 로 커넥션 풀을 변경할 때에도 애플리케이션의 코드가 변경된다.
- `DataSorce` 는 커넥션을 획득하는 방법을 추상화하는 인터페이스이다. 핵심 기능은 커넥션 조회 `getConnection()` 이다.
- 대부분의 커넥션 풀은 `DataSorce` 인터페이스를 이미 구현해두었다. 따라서 커넥션풀 구현 기술을 변경하고 싶으면 구현체를 갈아끼운다.
  - `JDBC DriverManager`를 이용하려면, `DataSorce` 인터페이스를 구현한`DriverManagerDataSource` 를 이용한다.
    
    ```
    DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();
    ```
  - `HikariCP` 를 통한 커넥션 풀링

    ```
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl(URL);
    dataSource.setUsername(USERNAME);
    dataSource.setPassword(PASSWORD);
    dataSource.setMaximumPoolSize(10);
    dataSource.setPoolName("MyPool");
    
    Connection con1 = dataSource.getConnection();
    Connection con2 = dataSource.getConnection();
    ```
    - 커넥션 풀에 커넥션을 채울때는 별도의 쓰레드를 사용한다.
    - 웹 애플리케이션에서 동시에 여러 요청이 들어오면 여러 쓰레드에서 커넥션 풀의 커넥션을 다양하게 가져간다.

- `DataSorce` 를 사용하면 **설정과 사용을 분리**할 수 있다.
</div>
</details>


### 트랜잭션 이해
- 트랜잭션은 모든 작업이 수행되거나, 모든 작업이 수행되지 않아야 한다.
  - 커밋 `Commit` : 모든 작업이 성공해서 데이터베이스에 정상 반영하는 것
  - 롤백 `Rollback` : 작업 중 하나라도 실패해서 거래 이전으로 되돌리는 것
- 트랜잭션 ACID
  - 원자성 : 트랜잭션 내에서 실행한 작업들은 마치 하나의 작업인 것처럼 모두 성공하거나 모두 실패한다.
  - 일관성 : 트랜잭션 실행 전과 후의 데이터베이스 상태는 일관적이어야 한다.
  - 격리성 : 동시에 실행되는 트랜잭션들이 서로에게 영향을 미치지 않도록 한다. 격리성은 동시성과 관련된 성능 이슈로 인해 트랜잭션 격리 수준을 선택할 수 있다.
  - 지속성 : 트랜잭션을 성공적으로 끝내면 그 결과가 항상 기록되어야 한다. 시스템에 문제가 발생해도 DB 로그를 통해 성공한 트랜잭션 내용을 복구한다.
- 커넥션을 생성하면, DB 내부에 세션이 생긴다. 세션은 트랜잭션을 시작하고, 커밋 또는 롤백을 통해 트랜잭션을 종료한다.