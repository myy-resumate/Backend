package dev.resumate.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResumeDTO {

    private String title;
    private LocalDateTime createdAt;
    private String organization;
    private String orgUrl;
    private LocalDate applyStart;
    private LocalDate applyEnd;

}
