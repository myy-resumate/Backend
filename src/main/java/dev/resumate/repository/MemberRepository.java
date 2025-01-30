package dev.resumate.repository;

import dev.resumate.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {


    boolean existsMemberByEmail(String email);

    Optional<Member> findByEmail(String email);
}
