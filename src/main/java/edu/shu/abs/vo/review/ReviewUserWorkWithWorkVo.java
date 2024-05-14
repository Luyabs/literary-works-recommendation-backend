package edu.shu.abs.vo.review;

import edu.shu.abs.entity.ReviewUserWork;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ReviewUserWorkWithWorkVo extends ReviewUserWork {
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
     * 总评分
     */
    private int sumRating;

    /**
     * 总评分用户数
     */
    private int sumRatingUserNumber;

    /**
     * 出版社
     */
    private String publisher;

    /**
     * 封面地址(URL)
     */
    private String coverLink;


}
