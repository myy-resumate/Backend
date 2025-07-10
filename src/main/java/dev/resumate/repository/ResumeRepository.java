package dev.resumate.repository;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    //부모(지원서) 조회
    @Query("SELECT new dev.resumate.repository.dto.ResumeDTO(r.title, r.createdAt, r.organization, r.orgUrl, r.applyStart, r.applyEnd) " +
            "FROM Resume r " +
            "WHERE r.id = :resumeId")
    Optional<ResumeDTO> findResume(@Param("resumeId") Long resumeId);

    //자식(자소서) 조회
    @Query("SELECT new dev.resumate.repository.dto.CoverLetterDTO(c.id, c.question, c.answer) " +
            "From CoverLetter c " +
            "WHERE c.resume.id = :resumeId")
    List<CoverLetterDTO> findCoverLetter(@Param("resumeId") Long resumeId);

    //자식(첨부 파일) 조회
    @Query("SELECT new dev.resumate.repository.dto.AttachmentDTO(a.id, a.fileName, a.url) " +
            "FROM Attachment a " +
            "WHERE a.resume.id = :resumeId")
    List<AttachmentDTO> findAttachment(@Param("resumeId") Long resumeId);

    //자식(태그) 조회
    @Query("SELECT new dev.resumate.repository.dto.TagDTO(t.id, tag.name) " +
            "FROM Tag tag " +
            "JOIN tag.taggings t " +  //dto를 받을 땐 join fetch 사용 못함. 어차피 tagging에 조회할 필드가 없으니 괜찮다.
            "WHERE t.resume.id = :resumeId")
    List<TagDTO> findTag(@Param("resumeId") Long resumeId);

    //지원서 목록 조회
    @Query("select distinct r " +
            "from Resume r " +
            "left join r.coverLetters c " +
            "left join r.taggings t " +
            "left join t.tag tag " +
            "where r.member = :member")
    Slice<Resume> findAllResume(@Param("member")Member member, Pageable pageable);

    //캘린더 조회
    @Query("select r from Resume r where r.member = :member and r.applyEnd between :start and :end")
    List<Resume> findResumeByApplyEndAndMember(@Param("member") Member member, @Param("start") LocalDate start, @Param("end") LocalDate end);

    //마감 공고 조회 - 중복 내용을 거르기 위해 dto로 받기
    @Query("select distinct new dev.resumate.repository.dto.DeadlineDTO(r.organization, r.orgUrl) " +
            "from Resume r " +
            "where r.member = :member and r.applyEnd >= :today order by r.applyEnd asc")
    List<DeadlineDTO> findDeadlineResume(@Param("member") Member member, @Param("today") LocalDate today, Pageable pageable);

    //태그로 검색 - 태그, 태깅에 인덱스 사용
    @Query("select r from Resume r " +
            "join r.taggings t " +
            "join t.tag tag " +
            "where tag.member = :member and tag.name in :tags " +
            "group by r.id " +
            "having count(distinct tag.id) = :tagCount")
    Slice<Resume> findByTag(@Param("member") Member member, @Param("tags") List<String> tags, @Param("tagCount") int tagCount, Pageable pageable);

    //지원서 검색
    @Query(value = "select r.* " +
            "from resume r " +
            "where match(r.title, r.organization) " +
            "against(:keyword in natural language mode) " +
            "and r.member_id = :memberId " +
            "union " +
            "select r.* " +
            "from resume r " +
            "join cover_letter c on r.resume_id = c.resume_id " +
            "where match(c.question, c.answer) " +
            "against(:keyword in natural language mode) " +
            "and r.member_id = :memberId;"
            , nativeQuery = true)
    Slice<Resume> findByKeyword(@Param("memberId") Long memberId, @Param("keyword") String keyword, Pageable pageable);

    //지원서 검색 v2
    @Query(value = "select r.* " +
            "from resume r " +
            "join resume_search rs on r.resume_search_id = rs.resume_search_id " +
            "where match(rs.title, rs.organization, rs.questions, rs.answers) " +
            "against(:keyword in natural language mode) " +
            "and r.member_id = :memberId;",
            nativeQuery = true)
    Slice<Resume> findByKeywordV2(@Param("memberId") Long memberId, @Param("keyword") String keyword, Pageable pageable);
}
