package dev.resumate.domain;

import dev.resumate.domain.common.BaseTimeEntity;
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
@Table(indexes = {  //조회보다 태깅 추가가 더 많이 일어난다면 인덱스 지우기
        @Index(name = "tag_resume_index", columnList = "tag_id, resume_id")
})
public class Tagging extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id")
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }
}
