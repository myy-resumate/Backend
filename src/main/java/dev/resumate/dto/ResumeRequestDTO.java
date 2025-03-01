package dev.resumate.dto;

import dev.resumate.repository.dto.TagDTO;
import jakarta.validation.constraints.NotNull;
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
        private List<String> tags = new ArrayList<>();  //null 가능
        private String organization;
        private String orgURl;
        private LocalDate applyStart;
        private LocalDate applyEnd;
        private List<CoverLetterDTO> coverLetterDTOS = new ArrayList<>();  //null 가능
    }

    @Getter
    public static class UpdateDTO {

        private String title;
        private List<TagDTO> tags = new ArrayList<>();  //null 가능
        private String organization;
        private String orgURl;
        private LocalDate applyStart;
        private LocalDate applyEnd;
        private List<CoverLetterDTO> coverLetterDTOS = new ArrayList<>();  //null 가능
    }

    @Getter
    public static class CoverLetterDTO{

        private Long coverLetterId;
        private String question;
        private String answer;
    }

}
