package dev.resumate.repository;

import dev.resumate.domain.Member;
import dev.resumate.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    boolean existsByNameAndMember(String name, Member member);

    Tag findByName(String name);

    Optional<Tag> findByNameAndMember(String name, Member member);
}
