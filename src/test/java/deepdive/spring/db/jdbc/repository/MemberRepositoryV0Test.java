package deepdive.spring.db.jdbc.repository;

import deepdive.spring.db.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV2", 10000);
        repository.save(member);

        // findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("member == findMember {}", member == findMember); // 다른 인스턴스다
        log.info("member eqauls findMember {}", member.equals(findMember)); // 값이 같다
        Assertions.assertThat(findMember).isEqualTo(member);
    }
}