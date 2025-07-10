package dev.resumate.domain;

import dev.resumate.domain.common.BaseTimeEntity;
import dev.resumate.dto.ResumeRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResumeSearch extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_search_id")
    private Long id;

    @Column(length = 100)
    private String title;

    @Column(length = 100)
    private String organization;

    @Column(columnDefinition = "TEXT")
    private String questions;

    @Column(columnDefinition = "TEXT")
    private String answers;

    public void setResumeSearch(ResumeRequestDTO.UpdateDTO request) {
        this.title = request.getTitle();
        this.organization = request.getOrganization();
        StringBuilder questions = new StringBuilder();
        StringBuilder answers = new StringBuilder();
        for (ResumeRequestDTO.CoverLetterDTO coverLetterDTO : request.getCoverLetterDTOS()) {
            questions.append(coverLetterDTO.getQuestion()).append(" ");
            answers.append(coverLetterDTO.getAnswer()).append(" ");
        }
        this.questions = questions.toString();
        this.answers = answers.toString();
    }
}
