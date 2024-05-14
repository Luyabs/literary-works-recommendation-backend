package edu.shu.abs.vo.algorithm.recommend;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LfmWorkSimilarQueryVo {
    /**
     * 文学作品Id
     */
    private Long workId;

    /**
     * 筛选前k个元素
     */
    private Integer k;
}
