package dev.resumate.common.redis.domain;

import dev.resumate.dto.ResumeResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "RecentResume")
@Getter
@Builder
@AllArgsConstructor
public class RecentResume {

    @Id
    private Long resumeId;
    private String thumbnail;
}
