package edu.shu.abs.vo.work;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WorkNewPostVo {
    /**
     * 标签
     */
    private String tags;

    /**
     * 作品名
     */
    private String workName;

    /**
     * 作者
     */
    private String author;

    /**
     * 作品简介
     */
    private String introduction;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 封面地址
     */
    private String coverLink;
}
