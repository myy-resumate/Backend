package dev.resumate.converter;

import dev.resumate.domain.Resume;
import dev.resumate.dto.HomeResponseDTO;

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
}
