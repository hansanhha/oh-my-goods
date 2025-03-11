package co.ohmygoods.coupon.repository.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

public class CouponRepositoryUtils {

    public static <T> Slice<T> createSlicePaginationResult(List<T> results, Pageable pageable) {
        boolean hasNext = results.size() > pageable.getPageSize();

        if (!results.isEmpty() && hasNext) {
            results.removeLast();
        }

        return new SliceImpl<>(results, pageable, hasNext);
    }
}
