package dev.resumate.service;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.domain.Tag;
import dev.resumate.domain.Tagging;
import dev.resumate.repository.TagRepository;
import dev.resumate.repository.TaggingRepository;
import dev.resumate.repository.dto.TagDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaggingService {

    private final TaggingRepository taggingRepository;
    private final TagRepository tagRepository;

    //태그, 태깅 저장
    @Transactional
    public void saveTagAndTagging(List<String> tags, Member member, Resume resume) {

        for (String tagName : tags) {
            Tag tag = saveTag(tagName, member);
            saveTagging(resume, tag);
        }
    }

    //이미 있으면 태그 반환, 없으면 저장
    @Transactional
    public Tag saveTag(String tagName, Member member) {

        return tagRepository.findByNameAndMember(tagName, member)
                .orElseGet(() -> tagRepository.save(Tag.builder()
                        .name(tagName)
                        .member(member)
                        .build()));
    }

    @Transactional
    public void saveTagging(Resume resume, Tag tag) {

        Tagging tagging = Tagging.builder()
                .tag(tag)
                .build();
        resume.addTagging(tagging);  //양방향 편의 메소드
        taggingRepository.save(tagging);
    }
}
