package dev.resumate.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DeadlineDTO {

    private String organization;
    private String orgUrl;
}
