package edu.shu.abs.vo.algorithm;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TestMetricsVo {
    /**
     * 平均损失
     */
    private Double avgLoss;

    /**
     * 平均准确率
     */
    private Double avgAccuracy;
}
