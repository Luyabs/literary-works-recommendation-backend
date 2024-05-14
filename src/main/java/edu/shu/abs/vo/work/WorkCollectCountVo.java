package edu.shu.abs.vo.work;

import edu.shu.abs.entity.Work;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WorkCollectCountVo extends Work {
    /**
     * 平被收藏次数 (按人数算)
     */
    private Long collectUserCount;
}
