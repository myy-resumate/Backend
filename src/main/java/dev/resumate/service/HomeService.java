package dev.resumate.service;

import dev.resumate.converter.HomeConverter;
import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import dev.resumate.dto.HomeResponseDTO;
import dev.resumate.repository.ResumeRepository;
import dev.resumate.repository.dto.DeadlineDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    /**
     * 마감 공고 조회
     * @param member
     * @return
     */
    public HomeResponseDTO.DeadlineListDTO getDeadline(Member member) {

        //상위 5개만 조회하기 위한 PageRequest 구현체
        List<DeadlineDTO> deadlineDTOS = resumeRepository.findDeadlineResume(member, LocalDate.now(), PageRequest.of(0, 5));

        return HomeResponseDTO.DeadlineListDTO.builder()
                .deadlineDTOS(HomeConverter.toDeadlineDTO(deadlineDTOS))
                .build();
    }
}
