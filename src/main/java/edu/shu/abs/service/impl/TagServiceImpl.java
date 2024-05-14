package edu.shu.abs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.shu.abs.common.base.BaseServiceImpl;
import edu.shu.abs.entity.Tag;
import edu.shu.abs.mapper.TagMapper;
import edu.shu.abs.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author abstraction
 */
@Service
public class TagServiceImpl extends BaseServiceImpl<TagMapper, Tag> implements TagService {
    @Autowired
    private TagMapper tagMapper;

    /**
     * 保存tag并返回tagId
     * @param tagName 标签名 如果表中不存在则新增一项
     * @return tagId
     */
    @Override
    public Long saveIfNotExist(String tagName) {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.eq("tag_name", tagName);
        boolean isExisted = tagMapper.exists(wrapper);
        if (!isExisted) { // 不存在这个tag则新增该tag
            tagMapper.insert(new Tag().setTagName(tagName));
        }
        return tagMapper.selectList(wrapper).get(0).getTagId();
    }
}
