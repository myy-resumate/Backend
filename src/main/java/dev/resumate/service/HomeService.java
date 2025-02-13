package dev.resumate.service;

import dev.resumate.converter.HomeConverter;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.HomeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final ResumeRepository resumeRepository;

    /**
     * 캘린더 월별 조회
     * @param member
     * @param startDate
     * @return
     */
    public HomeResponseDTO.CalendarDTO getCalendar(Member member, LocalDate startDate) {

        LocalDate endDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), startDate.lengthOfMonth());
        List<Resume> resumes = resumeRepository.findResumeByApplyEndAndMember(member, startDate, endDate);

        return HomeResponseDTO.CalendarDTO.builder()
                .dateDTOS(HomeConverter.toDateDTO(resumes))
                .build();
    }
}
