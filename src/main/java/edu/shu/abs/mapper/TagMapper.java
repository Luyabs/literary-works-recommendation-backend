package edu.shu.abs.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.shu.abs.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author abstraction
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {

    void selectOne(QueryWrapper<Tag> wrapper);
}
