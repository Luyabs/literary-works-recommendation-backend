package edu.shu.abs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import edu.shu.abs.common.Page;
import edu.shu.abs.entity.ReviewUserWork;
import edu.shu.abs.vo.review.ReviewUserWorkWithUserVo;
import edu.shu.abs.vo.review.ReviewUserWorkWithWorkVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Mapper
public interface ReviewUserWorkMapper extends BaseMapper<ReviewUserWork> {
    @Select("""
            select ruw.review_id, ruw.user_id, ruw.work_id, ruw.rating, ruw.content,
            ruw.create_time, ruw.update_time, u.username
            from review_user_work ruw
            join user u on ruw.user_id = u.user_id
            ${ew.customSqlSegment}
            """)
    Page<ReviewUserWorkWithUserVo> selectReviewUserWorkExtraVoPage(Page<Object> objectPage, @Param(Constants.WRAPPER) QueryWrapper<ReviewUserWork> wrapper);

    @Select("""
            select ruw.review_id, ruw.user_id, ruw.work_id, ruw.rating, ruw.content, ruw.create_time, ruw.update_time, w.tags, w.work_name, w.author, w.introduction, w.publisher, w.sum_rating, w.sum_rating_user_number, w.cover_link
            from review_user_work ruw join work w on ruw.work_id = w.work_id
            where ruw.user_id = ${user_id}
            order by ruw.rating desc, ruw.update_time desc
            """)
    IPage<ReviewUserWorkWithWorkVo> selectReviewUserWorkWithWork(Page<Object> objectPage, @Param("user_id") Long userId);
}
