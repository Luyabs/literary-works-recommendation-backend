package edu.shu.abs.vo.user;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserPrivacyVo {
    /**
     * 个人信息是否公开
     */
    private Boolean isInfoPublic;

    /**
     * 个人评价是否公开
     */
    private Boolean isCommentPublic;
}
