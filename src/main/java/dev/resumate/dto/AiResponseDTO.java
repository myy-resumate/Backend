package dev.resumate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class AiResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SimilarQuestionDTO {

        private List<QuestionAnswerDTO> questionAnswerDTOList;
}

    @Getter
    @Builder
    @AllArgsConstructor
    public static class QuestionAnswerDTO {

        private Long resumeId;
        private String question;
        private String answer;
    }
}
