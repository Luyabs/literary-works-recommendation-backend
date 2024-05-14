package edu.shu.abs.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.shu.abs.entity.Work;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.shu.abs.vo.history.HistoryVo;
import edu.shu.abs.vo.work.WorkCollectCountVo;
import edu.shu.abs.vo.work.WorkRatingVo;
import edu.shu.abs.vo.work.WorkVisitCountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Mapper
public interface WorkMapper extends BaseMapper<Work> {
    @Select("""
            select history_id, user_id, his.work_id, his.visit_count, his.create_time, his.update_time,
                tags, work_name, author, introduction, publisher, cover_link
            from history_user_work his join work on his.work_id = work.work_id
            where user_id = ${user_id} and work.is_deleted = 0
            order by update_time desc
            """)
    IPage<HistoryVo> selectHistoryUserWorkPage(Page<HistoryVo> historyVoPage, @Param("user_id") long userId);

    @Select("""
            select sum_rating / sum_rating_user_number as avg_rating, work_id, tags, work_name, author, introduction,
                   publisher, sum_rating, sum_rating_user_number, cover_link, create_time, update_time, is_deleted
            from work
            where is_deleted = 0 && sum_rating_user_number >= 20
            order by avg_rating desc, sum_rating_user_number desc
            """)
    IPage<WorkRatingVo> selectHighestRating(edu.shu.abs.common.Page<WorkRatingVo> objectPage);

    @Select("""
            select sum_rating / sum_rating_user_number as avg_rating, work_id, tags, work_name, author, introduction,
                   publisher, sum_rating, sum_rating_user_number, cover_link, create_time, update_time, is_deleted
            from work
            where is_deleted = 0
            order by sum_rating_user_number desc, avg_rating desc
            """)
    IPage<WorkRatingVo> selectMostRatingPage(edu.shu.abs.common.Page<WorkRatingVo> objectPage);

    @Select("""
            select sum_rating / sum_rating_user_number as avg_rating, work_id, tags, work_name, author, introduction,
                   publisher, sum_rating, sum_rating_user_number, cover_link, create_time, update_time, is_deleted
            from work
            where is_deleted = 0
            order by sum_rating_user_number desc, avg_rating desc
            limit ${num}
            """)
    List<WorkRatingVo> selectMostRatingLimitNum(@Param("num") Integer num);

    @Select("""
            select  visit_count, w.work_id, tags, work_name, author, introduction, publisher,
                    sum_rating, sum_rating_user_number, cover_link, w.create_time, w.update_time, is_deleted
            from (
                select work_id, sum(visit_count) as visit_count
                from history_user_work
                group by work_id
            ) his join work w on his.work_id = w.work_id
            where w.is_deleted = 0
            order by visit_count desc, sum_rating / sum_rating_user_number desc, sum_rating_user_number desc
            """)
    IPage<WorkVisitCountVo> selectMostVisit(edu.shu.abs.common.Page<WorkVisitCountVo> objectPage);


    @Select("""
            select  collect_user_count, w.work_id, tags, work_name, author, introduction, publisher,
                    sum_rating, sum_rating_user_number, cover_link, w.create_time, w.update_time, is_deleted
            from (
                     select work_id, count(distinct c.owner_id) as collect_user_count
                     from record_collection_work rcw
                     join collection c on rcw.collection_id = c.collection_id
                     group by work_id
                 ) his join work w on his.work_id = w.work_id
            where w.is_deleted = 0
            order by collect_user_count desc, sum_rating / sum_rating_user_number desc, sum_rating_user_number desc
            """)
    IPage<WorkCollectCountVo> selectMostCollect(edu.shu.abs.common.Page<WorkCollectCountVo> objectPage);
}
