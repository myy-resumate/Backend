package dev.resumate.service;

import dev.resumate.domain.CoverLetter;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.repository.CoverLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
