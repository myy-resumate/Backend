package dev.resumate.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReadThumbnailDTO {

    private String title;
    private LocalDateTime createdAt;
    private String organization;
    private String orgUrl;
    private String question;
    private List<TagDTO> tags;

    public ReadThumbnailDTO(String title, LocalDateTime createdAt, String organization, String orgUrl, String question) {

        this.title = title;
        this.createdAt = createdAt;
        this.organization = organization;
        this.orgUrl = orgUrl;
        this.question = question;
    }
}
