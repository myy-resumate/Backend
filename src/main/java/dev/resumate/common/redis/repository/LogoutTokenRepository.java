package dev.resumate.common.redis.repository;

import dev.resumate.common.redis.domain.LogoutToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogoutTokenRepository extends CrudRepository<LogoutToken, String> {

    LogoutToken findTokenByAccessToken(String accessToken);
}
