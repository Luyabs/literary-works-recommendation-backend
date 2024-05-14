package edu.shu.abs.vo.work;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WorkEditVo extends WorkNewPostVo {
    /**
     * 作品ID
     */
    private Long workId;
}
