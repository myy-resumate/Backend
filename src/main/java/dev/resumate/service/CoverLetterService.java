package dev.resumate.service;

import dev.resumate.domain.CoverLetter;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.repository.CoverLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CoverLetterService {

    private final CoverLetterRepository coverLetterRepository;

    public void saveCoverLetter(List<ResumeRequestDTO.CoverLetterDTO> coverLetterDTOS, Resume resume) {

        for (ResumeRequestDTO.CoverLetterDTO coverLetterDTO : coverLetterDTOS) {
            CoverLetter coverLetter = CoverLetter.builder()
                    .question(coverLetterDTO.getQuestion())
                    .answer(coverLetterDTO.getAnswer())
                    .resume(resume)
                    .build();
            coverLetterRepository.save(coverLetter);
        }
    }

    @Transactional //자소서 삭제 후, 다시 저장
    public void updateCoverLetter(List<ResumeRequestDTO.CoverLetterDTO> coverLetterDTOS, Resume resume) {

        coverLetterRepository.deleteAllByResume(resume);
        saveCoverLetter(coverLetterDTOS, resume);
    }
}
