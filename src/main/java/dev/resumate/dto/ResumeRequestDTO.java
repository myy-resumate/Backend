package dev.resumate.dto;

import dev.resumate.repository.dto.TagDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResumeRequestDTO {

    @Getter
    public static class CreateDTO {

        private String title;
        private List<String> tags = new ArrayList<>();
        private String organization;
        private String orgURl;
        private LocalDate applyStart;
        private LocalDate applyEnd;
        private List<CoverLetterDTO> coverLetterDTOS = new ArrayList<>();
    }

    @Getter
    public static class UpdateDTO {

        private String title;
        private List<TagDTO> tags = new ArrayList<>();
        private String organization;
        private String orgURl;
        private LocalDate applyStart;
        private LocalDate applyEnd;
        private List<CoverLetterDTO> coverLetterDTOS = new ArrayList<>();
    }

    @Getter
    public static class CoverLetterDTO{

        private String question;
        private String answer;
    }

}
