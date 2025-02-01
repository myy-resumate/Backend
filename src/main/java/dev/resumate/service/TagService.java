package dev.resumate.service;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.domain.Tag;
import dev.resumate.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TaggingService taggingService;

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
}
