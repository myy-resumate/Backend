package dev.resumate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ResumeRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor  //생성자가 있어야 한다!!
    public static class CreateDTO {

        private String title;
        private List<String> tags;
        private String organization;
        private String orgURl;
        private LocalDate applyStart;
        private LocalDate applyEnd;
        private List<CoverLetterDTO> coverLetterDTOS;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor  //얘도 생성자가 있어야!
    public static class CoverLetterDTO{

        private String question;
        private String answer;
    }

}
