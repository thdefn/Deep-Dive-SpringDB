

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
#### JDBC `Connection` 인터페이스
  - jdbc는 java.sql.Connection 표준 커넥션 인터페이스를 정의함
  - H2 데이터베이스 드라이버는 JDBC Connection 인터페이스를 구현한 `org.h2.jdbc.jdbcConnection` 구현체를 제공
#### JDBC `DriverManager`
  - 애플리케이션 로직에서 커넥션이 필요하면 `DriverManager.getConnection()` 호출
  - `DriverManager`는 라이브러리에 등록된 드라이버 목록을 자동으로 인식 
  - 이 드라이버들에게 순차적으로 커넥션을 획득할 수 있는지 확인함
  - 이렇게 찾은 커넥션 구현체가 클라이언트에게 반환됨