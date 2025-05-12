package dev.resumate.repository;

import dev.resumate.domain.CoverLetter;
import dev.resumate.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoverLetterRepository extends JpaRepository<CoverLetter, Long> {

    List<CoverLetter> findAllByResume(Resume resume);
}
