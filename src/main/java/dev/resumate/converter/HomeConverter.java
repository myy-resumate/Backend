package dev.resumate.converter;

import dev.resumate.domain.Resume;
import dev.resumate.dto.HomeResponseDTO;
import dev.resumate.repository.dto.DeadlineDTO;

import java.util.List;
import java.util.stream.Collectors;

public class HomeConverter {

    public static List<HomeResponseDTO.DateDTO> toDateDTO(List<Resume> resumes) {

        return resumes.stream()
                .map(resume -> HomeResponseDTO.DateDTO.builder()
                        .organization(resume.getOrganization())
                        .applyEnd(resume.getApplyEnd())
                        .build())
                .collect(Collectors.toList());
    }

    public static List<HomeResponseDTO.DeadlineDTO> toDeadlineDTO(List<DeadlineDTO> resumes) {

        return resumes.stream()
                .map(resume -> HomeResponseDTO.DeadlineDTO.builder()
                        .organization(resume.getOrganization())
                        .orgUrl(resume.getOrgUrl())
                        .build())
                .collect(Collectors.toList());
    }
}
