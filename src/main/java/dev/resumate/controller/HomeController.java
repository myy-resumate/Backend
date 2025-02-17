package dev.resumate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.resumate.apiPayload.response.ApiResponseDTO;
import dev.resumate.common.auth.AuthUser;
import dev.resumate.domain.Member;
import dev.resumate.dto.HomeResponseDTO;
import dev.resumate.dto.ResumeResponseDTO;
import dev.resumate.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    /**
     * 캘린더 월별 조회
     * @param member
     * @param startDate
     * @return
     */
    @GetMapping("/resumes/calendar")
    public ApiResponseDTO<HomeResponseDTO.CalendarDTO> getCalendar(@AuthUser Member member, @RequestParam LocalDate startDate) {
        return ApiResponseDTO.onSuccess(homeService.getCalendar(member, startDate));
    }

    /**
     * 마감 공고 조회
     * @param member
     * @return
     */
    @GetMapping("/resumes/deadline")
    public ApiResponseDTO<HomeResponseDTO.DeadlineListDTO> getDeadline(@AuthUser Member member) {
        return ApiResponseDTO.onSuccess(homeService.getDeadline(member));
    }

    /**
     * 최근 본 지원서 조회
     * @param member
     * @return
     */
    @GetMapping("/resumes/recent")
    public ApiResponseDTO<List<ResumeResponseDTO.ReadThumbnailDTO>> getRecentResume(@AuthUser Member member) {
        return ApiResponseDTO.onSuccess(homeService.getRecentResume(member));
    }
}
