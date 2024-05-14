package edu.shu.abs.vo.collection;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CollectionNewPostVo {
    /**
     * 收藏夹名
     */
    private String collectionName;

    /**
     * 收藏夹简介
     */
    private String introduction;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 是否为默认收藏夹(仅一个收藏夹可作为默认收藏夹)
     */
    private Boolean isDefaultCollection;
}
