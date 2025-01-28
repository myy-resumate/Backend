package dev.resumate.config.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "RefreshToken", timeToLive = 7 * 24 * 60 * 60)  //일주일 뒤에 만료
@Getter
@Builder
@AllArgsConstructor
public class RefreshToken {

    @Id
    private Long id;
    private String refreshToken;
}
