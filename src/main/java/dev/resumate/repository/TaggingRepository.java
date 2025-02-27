package dev.resumate.repository;

import dev.resumate.domain.Resume;
import dev.resumate.domain.Tagging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaggingRepository extends JpaRepository<Tagging, Long> {

    void deleteAllByResume(Resume resume);

    List<Tagging> findAllByResume(Resume resume);
}
