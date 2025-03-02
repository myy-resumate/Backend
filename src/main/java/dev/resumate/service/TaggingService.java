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

    @Transactional
    public void deleteTagging(Resume resume) {
        taggingRepository.deleteAllByResume(resume);
    }

    //태깅 수정
    @Transactional
    public void updateTagging(List<TagDTO> tags, Member member, Resume resume) {

        List<Tagging> oldTaggings = taggingRepository.findAllByResume(resume);

        //Map으로 변환 - key=taggingId, value=tagging객체
        Map<Long, Tagging> oldTaggingMap = oldTaggings.stream().collect(Collectors.toMap(Tagging::getId, Function.identity()));

        //taggingId를 Set에 저장 - 삭제 대상 tagging
        Set<Long> taggingIdsToDelete = new HashSet<>(oldTaggingMap.keySet());

        for (TagDTO tagDTO : tags) {
            Tag tag = saveTag(tagDTO.getTagName(), member);

            if (oldTaggingMap.containsKey(tagDTO.getTaggingId())) {
                Tagging tagging = oldTaggingMap.get(tagDTO.getTaggingId());
                tagging.setTag(tag);  //변경감지
                taggingIdsToDelete.remove(tagDTO.getTaggingId());
            } else {
                saveTagging(resume, tag);
            }
        }

        //양방향 매핑된 resume의 tagging리스트에서도 요소 삭제
        resume.getTaggings().removeIf(tagging -> taggingIdsToDelete.contains(tagging.getId()));
        //set에 남아있는 tagging들 삭제
        taggingRepository.deleteAllById(taggingIdsToDelete);
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
