package dev.resumate.repository;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.dto.AttachmentDTO;
import dev.resumate.repository.dto.CoverLetterDTO;
import dev.resumate.repository.dto.ResumeDTO;
import dev.resumate.repository.dto.TagDTO;
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
    @Query("SELECT new dev.resumate.repository.dto.CoverLetterDTO(c.question, c.answer) " +
            "From CoverLetter c " +
            "WHERE c.resume.id = :resumeId")
    List<CoverLetterDTO> findCoverLetter(@Param("resumeId") Long resumeId);

    //자식(첨부 파일) 조회
    @Query("SELECT new dev.resumate.repository.dto.AttachmentDTO(a.fileName, a.url) " +
            "FROM Attachment a " +
            "WHERE a.resume.id = :resumeId")
    List<AttachmentDTO> findAttachment(@Param("resumeId") Long resumeId);

    //자식(태그) 조회
    @Query("SELECT new dev.resumate.repository.dto.TagDTO(tag.name) " +
            "FROM Tag tag " +
            "JOIN tag.taggings t " +  //dto를 받을 땐 join fetch 사용 못함. 어차피 tagging에 조회할 필드가 없으니 괜찮다.
            "WHERE t.resume.id = :resumeId")
    List<TagDTO> findTag(@Param("resumeId") Long resumeId);

    //지원서 목록 조회
    @Query("select distinct r " +
            "from Resume r " +
            "join r.coverLetters c " +
            "join r.taggings t " +
            "join t.tag tag " +
            "where r.member = :member")
    Slice<Resume> findAllResume(@Param("member")Member member, Pageable pageable);

    //캘린더 조회
    @Query("select r from Resume r where r.member = :member and r.applyEnd between :start and :end")
    List<Resume> findResumeByApplyEndAndMember(@Param("member") Member member, @Param("start") LocalDate start, @Param("end") LocalDate end);
}
