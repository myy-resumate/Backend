package dev.resumate.repository.queryDsl;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import dev.resumate.common.slice.SliceUtil;
import dev.resumate.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ResumeRepositoryImpl implements ResumeRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Slice<Resume> findByTag(Member member, List<String> tags, Pageable pageable) {

        QResume resume = QResume.resume;
        QTag tag = QTag.tag;
        QTagging tagging = QTagging.tagging;

        List<BooleanExpression> exists = new ArrayList<>();
        addExistCondition(member, tags, tagging, tag, resume, exists);

        List<Resume> result = jpaQueryFactory
                .selectFrom(resume)
                .where(exists.toArray(new Predicate[0]))
                .orderBy(resume.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)  //hasNext 판단하기 위해 페이지 사이즈 1 크게
                .fetch();

        return SliceUtil.toSlice(result, pageable);
    }

    //exist문 추가
    private static void addExistCondition(Member member, List<String> tags, QTagging tagging, QTag tag, QResume resume, List<BooleanExpression> exists) {
        for (String tagName : tags) {
            BooleanExpression exist = JPAExpressions
                    .selectOne()
                    .from(tagging)
                    .join(tag).on(tagging.tag.eq(tag))
                    .where(
                            tagging.resume.eq(resume),
                            tag.member.eq(member),
                            tag.name.eq(tagName)
                    )
                    .exists();
            exists.add(exist);
        }
    }
}
