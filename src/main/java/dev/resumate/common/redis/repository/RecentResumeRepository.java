package dev.resumate.common.redis.repository;

import dev.resumate.common.redis.domain.RecentResume;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecentResumeRepository extends CrudRepository<RecentResume, Long> {
}
