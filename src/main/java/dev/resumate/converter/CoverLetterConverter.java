package dev.resumate.converter;

import dev.resumate.domain.CoverLetter;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;

import java.util.ArrayList;
import java.util.List;

public class CoverLetterConverter {

    public static CoverLetter toCoverLetter(ResumeRequestDTO.CoverLetterDTO coverLetterDTO) {

        return CoverLetter.builder()
                .question(coverLetterDTO.getQuestion())
                .answer(coverLetterDTO.getAnswer())
                .build();
    }
}
