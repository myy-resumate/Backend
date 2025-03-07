package dev.resumate.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CoverLetterDTO {

    private Long coverLetterId;
    private String question;
    private String answer;
}
