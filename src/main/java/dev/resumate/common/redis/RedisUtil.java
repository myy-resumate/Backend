package dev.resumate.common.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * dto를 json으로 직렬화
     * @param dto
     * @return
     * @throws JsonProcessingException
     */
    public <T> String toJson(T dto) throws JsonProcessingException {

        return objectMapper.writeValueAsString(dto);
    }

    /**
     * 직렬화된 json sorted set을 dto리스트로 역직렬화
     * @param jsonSet
     * @param classType
     * @return
     * @param <T>
     */
    public <T> List<T> toDTO(List<String> jsonSet, Class<T> classType) {

        return jsonSet.stream().map(json -> {
            try {
                return objectMapper.readValue(json, classType);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * sorted set에 저장
     * @param key
     * @param score
     * @param member
     */
    public Set<String> addSortedSet(String key, double score, String member, int limit) {

        redisTemplate.opsForZSet().add(key, member, score);
        Set<String> oldestSet = redisTemplate.opsForZSet().range(key, 0, -1 * (limit + 1));

        //limit개까지만 저장. limit를 넘기는 오래된 데이터 삭제
        redisTemplate.opsForZSet().removeRange(key, 0, -1 * (limit + 1));
        return oldestSet;
    }

    /**
     * sorted set 조회
     * @param key
     * @param count
     * @return
     */
    public Set<String> getSortedSet(String key, int count) {

        return redisTemplate.opsForZSet().reverseRangeByScore(key, 0, Double.MAX_VALUE, 0, count);
    }
}
