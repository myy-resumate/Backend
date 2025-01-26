package dev.resumate.repository;

import dev.resumate.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    public boolean existsMemberByEmail(String email);
}
