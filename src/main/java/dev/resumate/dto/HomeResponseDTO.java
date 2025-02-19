package dev.resumate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class HomeResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CalendarDTO {

        private List<DateDTO> dateDTOS;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    public static class DateDTO {

        private String organization;
        private LocalDate applyEnd;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DeadlineListDTO {

        private List<DeadlineDTO> deadlineDTOS;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DeadlineDTO {

        private String organization;
        private String orgUrl;
    }
}
