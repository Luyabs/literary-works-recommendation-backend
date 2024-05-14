package edu.shu.abs.vo.work;

import edu.shu.abs.entity.Work;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WorkVisitCountVo extends Work {
    /**
     * 被浏览次数
     */
    private Long visitCount;
}
