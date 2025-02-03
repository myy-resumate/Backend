package dev.resumate.domain;

import dev.resumate.domain.common.BaseTimeEntity;
import dev.resumate.dto.ResumeRequestDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "resume_id")
    private Long id;

    private String title;

    private String organization;

    private String orgUrl;

    private LocalDate applyStart;

    private LocalDate applyEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //cascade를 하기 위해 양방향 매핑 - 자소서와 첨부파일은 오직 지원서하고만 연관
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<CoverLetter> coverLetters = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();

    public void setResume(ResumeRequestDTO.UpdateDTO request) {
        this.title = request.getTitle();
        this.organization = request.getOrganization();
        this.orgUrl = request.getOrgURl();
        this.applyStart = request.getApplyStart();
        this.applyEnd = request.getApplyEnd();
    }
}
