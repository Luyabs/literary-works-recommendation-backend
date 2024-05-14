package edu.shu.abs.vo.review;

import edu.shu.abs.entity.ReviewUserWork;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReviewUserWorkWithUserVo extends ReviewUserWork {
    /**
     * 用户名
     */
    private String username;
}
