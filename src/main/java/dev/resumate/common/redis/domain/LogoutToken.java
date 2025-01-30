package dev.resumate.common.redis.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "LogoutToken", timeToLive = 60 * 60)  //만료시간 1시간
@Getter
@Builder
@AllArgsConstructor
public class LogoutToken {  //로그아웃된 액세스 토큰 - 블랙리스트로 저장

    @Id
    private String accessToken;  //로그아웃을 여러번 할 수도 있으므로 토큰을 id로 지정
    private Long memberId;
}
