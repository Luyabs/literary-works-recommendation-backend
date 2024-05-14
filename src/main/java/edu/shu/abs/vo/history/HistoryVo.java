package edu.shu.abs.vo.history;

import edu.shu.abs.entity.HistoryUserWork;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class HistoryVo extends HistoryUserWork {
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
     * 封面地址(URL)
     */
    private String coverLink;
}
