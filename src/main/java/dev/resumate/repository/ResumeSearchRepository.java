package dev.resumate.repository;

import dev.resumate.domain.ResumeSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResumeSearchRepository extends JpaRepository<ResumeSearch, Long> {
}
