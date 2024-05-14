package edu.shu.abs.vo.work;

import edu.shu.abs.entity.Work;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WorkRatingVo extends Work {
    /**
     * 平均分
     */
    private Double avgRating;
}
