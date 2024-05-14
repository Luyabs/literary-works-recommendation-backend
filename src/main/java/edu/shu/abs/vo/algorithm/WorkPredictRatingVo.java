package edu.shu.abs.vo.algorithm;

import edu.shu.abs.entity.Work;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WorkPredictRatingVo extends Work {
    /**
     * 预测评分
     */
    private Double ratingHat;
}
