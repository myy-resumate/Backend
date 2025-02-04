package dev.resumate.converter;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.ResumeRequestDTO;

import java.util.ArrayList;

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
}
