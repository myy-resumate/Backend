package dev.resumate.repository.queryDsl;

import dev.resumate.domain.Member;
import dev.resumate.domain.Resume;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ResumeRepositoryCustom {

    Slice<Resume> findByTag(Member member, List<String> tags, Pageable pageable);
}
