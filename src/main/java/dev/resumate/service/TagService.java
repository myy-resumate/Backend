package dev.resumate.service;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.domain.Tag;
import dev.resumate.repository.TagRepository;
import dev.resumate.repository.TaggingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TaggingService taggingService;
    private final TaggingRepository taggingRepository;

    public void saveTag(List<String> tags, Member member, Resume resume) {

        for (String tagName : tags) {
            if (!tagRepository.existsByName(tagName)) {
                Tag tag = Tag.builder()
                        .name(tagName)
                        .member(member)
                        .build();
                taggingService.saveTagging(resume, tagRepository.save(tag));
            } else {
                Tag tag = tagRepository.findByName(tagName);
                taggingService.saveTagging(resume, tag);
            }
        }
    }

    public void updateTag(List<String> tags, Member member, Resume resume) {

        //태깅 삭제
        taggingRepository.deleteAllByResume(resume);
        saveTag(tags, member, resume);
    }
}
