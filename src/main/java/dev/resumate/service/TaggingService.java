package dev.resumate.service;

import dev.resumate.domain.Resume;
import dev.resumate.domain.Tag;
import dev.resumate.domain.Tagging;
import dev.resumate.repository.TaggingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaggingService {

    private final TaggingRepository taggingRepository;

    public void saveTagging(Resume resume, Tag tag) {

        Tagging tagging = Tagging.builder()
                .resume(resume)
                .tag(tag)
                .build();
        taggingRepository.save(tagging);
    }

    public void deleteTagging(Resume resume) {
        taggingRepository.deleteAllByResume(resume);
    }
}
