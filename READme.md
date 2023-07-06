

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

- `ResultSet`은 다음과 같이 생긴 데이터 구조
- `Cursor` : `ResultSet` 내부에 있는 커서를 이동해서 데이터를 조회
  - `rs.next()` 를 호출하면 커서가 이동하고, row 존재 여부를 반환
    - `rs.next()` 결과가 `true`면 커서의 이동 결과 데이터가 있다는 뜻
    - `rs.next()` 결과가 `false`면 커서의 이동 결과 데이터가 없다는 뜻
</div>
</details>
