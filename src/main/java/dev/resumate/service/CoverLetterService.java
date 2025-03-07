package dev.resumate.service;

import dev.resumate.converter.CoverLetterConverter;
import dev.resumate.domain.*;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.repository.CoverLetterRepository;
import dev.resumate.repository.dto.CoverLetterDTO;
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
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;

    //자소서 수정
    @Transactional
    public void updateCoverLetters(List<ResumeRequestDTO.CoverLetterDTO> coverLetterDTOS, Resume resume) {

        List<CoverLetter> oldCoverLetters = coverLetterRepository.findAllByResume(resume);

        Map<Long, CoverLetter> oldCoverLettersMap = oldCoverLetters.stream().collect(Collectors.toMap(CoverLetter::getId, Function.identity()));

        //삭제 대상을 구하기 위한 set
        Set<Long> coverLetterIdsToDelete = new HashSet<>(oldCoverLettersMap.keySet());

        //기존 꺼는 수정하고, 새로운 건 추가
        for (ResumeRequestDTO.CoverLetterDTO coverLetterDTO : coverLetterDTOS) {

            if (oldCoverLettersMap.containsKey(coverLetterDTO.getCoverLetterId())) {
                CoverLetter coverLetter = oldCoverLettersMap.get(coverLetterDTO.getCoverLetterId());
                coverLetter.setQuestionAndAnswer(coverLetterDTO.getQuestion(), coverLetterDTO.getAnswer());
                resume.addCoverLetter(coverLetter);
                coverLetterIdsToDelete.remove(coverLetterDTO.getCoverLetterId());  //삭제 대상 set에서 제거
            } else {
                addCoverLetter(resume, coverLetterDTO);
            }
        }

        //set에 남은 자소서들 삭제
        //cascade, orphanRemoval 적용한 경우엔 리스트에서 제거해줘야 한다.
        resume.getCoverLetters().removeIf(coverLetter -> coverLetterIdsToDelete.contains(coverLetter.getId()));
    }

    @Transactional
    //자소서 수정 시 자소서 추가
    public void addCoverLetter(Resume resume, ResumeRequestDTO.CoverLetterDTO coverLetterDTO) {

        CoverLetter newCoverLetter = CoverLetter.builder()
                .question(coverLetterDTO.getQuestion())
                .answer(coverLetterDTO.getAnswer())
                .build();

        resume.addCoverLetter(newCoverLetter);
    }

}
