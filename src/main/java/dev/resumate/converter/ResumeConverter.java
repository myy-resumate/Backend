package dev.resumate.converter;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.repository.dto.AttachmentDTO;
import dev.resumate.repository.dto.CoverLetterDTO;
import dev.resumate.repository.dto.ResumeDTO;
import dev.resumate.repository.dto.TagDTO;

import java.util.ArrayList;
import java.util.List;

public class ResumeConverter {

    public static Resume toResume(ResumeRequestDTO.CreateDTO request, Member member) {

        return Resume.builder()
                .title(request.getTitle())
                .organization(request.getOrganization())
                .orgUrl(request.getOrgURl())
                .applyStart(request.getApplyStart())
                .applyEnd(request.getApplyEnd())
                .member(member)
                .coverLetters(new ArrayList<>())  //빌더패턴은 @AllArgsConstructor로 생성하기 때문에 필드에서 초기화한게 무시된다. 다시 세팅해야함
                .attachments(new ArrayList<>())
                .build();
    }

    public static ResumeResponseDTO.ReadResultDTO toReadResultDTO(ResumeDTO resumeDTO, List<CoverLetterDTO> coverLetterDTOS, List<AttachmentDTO> attachmentDTOS, List<TagDTO> tagDTOS) {
        return ResumeResponseDTO.ReadResultDTO.builder()
                .title(resumeDTO.getTitle())
                .createdAt(resumeDTO.getCreatedAt())
                .org(resumeDTO.getOrganization())
                .orgUrl(resumeDTO.getOrgUrl())
                .applyStart(resumeDTO.getApplyStart())
                .applyEnd(resumeDTO.getApplyEnd())
                .coverLetters(coverLetterDTOS)
                .attachments(attachmentDTOS)
                .tags(tagDTOS)
                .build();
    }

    public static ResumeResponseDTO.ReadThumbnailDTO toReadThumbnailDTO(Resume resume, List<TagDTO> tagDTOS) {

        String preview = "지원처: " + resume.getOrganization() + "\n지원처 링크: " + resume.getOrgUrl() +
                "\n자소서:" + resume.getCoverLetters().get(0).getQuestion();

        return ResumeResponseDTO.ReadThumbnailDTO.builder()
                .resumeId(resume.getId())
                .title(resume.getTitle())
                .createDate(resume.getCreatedAt().toLocalDate())
                .organization(resume.getOrganization())
                .preview(preview)
                .tags(tagDTOS)
                .build();
    }
}
