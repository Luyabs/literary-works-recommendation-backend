package edu.shu.abs.vo.algorithm.recommend;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LfmRecallQueryVo {
    /**
     * 筛选前k个元素
     */
    private Integer k;

    /**
     * 最低阈值
     */
    private Float threshold;

    /**
     * 混合权重
     */
    private Float mixWeight;
}
