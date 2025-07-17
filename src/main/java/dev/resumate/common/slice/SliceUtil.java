package dev.resumate.common.slice;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class SliceUtil {

    /**
     * 리스트를 Slice로 바꿔주는 메서드
     * @param result
     * @param pageable
     * @return
     * @param <T>
     */
    public static <T> Slice<T> toSlice(List<T> result, Pageable pageable) {
        boolean hasNext = result.size() > pageable.getPageSize();
        if (hasNext) {
            result.remove(result.size() - 1);
        }
        return new SliceImpl<>(result, pageable, hasNext);
    }
}
