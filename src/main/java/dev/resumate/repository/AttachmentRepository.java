package dev.resumate.repository;

import dev.resumate.domain.Attachment;
import dev.resumate.domain.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    void deleteAllByResume(Resume resume);

    List<Attachment> findAllByResume(Resume resume);
}
