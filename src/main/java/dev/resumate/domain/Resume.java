package dev.resumate.domain;

import dev.resumate.domain.common.BaseTimeEntity;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.repository.ResumeSearchRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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
    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)  //자식 리스트에서 삭제된 자식은 고아객체가 되어 db에서도 삭제된다.
    private List<CoverLetter> coverLetters = new ArrayList<>();

    @OneToMany(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "resume")
    private List<Tagging> taggings = new ArrayList<>();  //목록 조회에서 태깅 조회가 필요해서 어쩔 수 없이 양방향 매핑

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "resume_search_id")
    private ResumeSearch resumeSearch;

    //지원서 수정
    public void setResume(ResumeRequestDTO.UpdateDTO request) {
        this.title = request.getTitle();
        this.organization = request.getOrganization();
        this.orgUrl = request.getOrgURl();
        this.applyStart = request.getApplyStart();
        this.applyEnd = request.getApplyEnd();
    }

    //양방향 편의 메소드 - 자소서
    public void addCoverLetter(CoverLetter coverLetter) {
        this.coverLetters.add(coverLetter);
        coverLetter.setResume(this);
    }

    //양방향 편의 메소드 - 첨부 파일
    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
        attachment.setResume(this);
    }

    //양방향 편의 메소드 - 태깅
    public void addTagging(Tagging tagging) {
        System.out.println(this.taggings.size());
        this.taggings.add(tagging);
        tagging.setResume(this);
    }

    public void setResumeSearch(ResumeSearch resumeSearch) {
        this.resumeSearch = resumeSearch;
    }
}
