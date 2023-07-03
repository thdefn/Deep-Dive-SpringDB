

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