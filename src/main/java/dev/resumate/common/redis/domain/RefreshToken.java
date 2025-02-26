package dev.resumate.common.redis.domain;

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
    private String refreshToken;  //재발급 요청할 때 refreshToken밖에 줄 수 있는 정보가 없기 때문에 refreshToken을 식별자로 지정
    private Long memberId;
    private String email;
}
