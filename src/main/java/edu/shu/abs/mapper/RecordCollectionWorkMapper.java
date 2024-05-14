package edu.shu.abs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import edu.shu.abs.common.Page;
import edu.shu.abs.entity.RecordCollectionWork;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.shu.abs.entity.Work;
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
public interface RecordCollectionWorkMapper extends BaseMapper<RecordCollectionWork> {

    @Select("""
            select w.work_id, w.tags, w.work_name, w.author, w.introduction, w.publisher,
            w.sum_rating, w.sum_rating_user_number, w.cover_link, rcw.create_time, rcw.update_time
            from work w
            join record_collection_work rcw on w.work_id = rcw.work_id
            where rcw.collection_id = ${collectionId}
            order by rcw.update_time desc
            """)
    Page<Work> selectWorkPage(Page<Object> objectPage, @Param("collectionId") Long collectionId);
}
