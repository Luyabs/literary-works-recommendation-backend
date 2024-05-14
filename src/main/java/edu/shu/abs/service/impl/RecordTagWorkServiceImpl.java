package edu.shu.abs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.shu.abs.common.base.BaseServiceImpl;
import edu.shu.abs.entity.RecordTagWork;
import edu.shu.abs.mapper.RecordTagWorkMapper;
import edu.shu.abs.service.RecordTagWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author abstraction
 */
@Service
public class RecordTagWorkServiceImpl extends BaseServiceImpl<RecordTagWorkMapper, RecordTagWork> implements RecordTagWorkService {
    @Autowired
    private RecordTagWorkMapper recordTagWorkMapper;

    /**
     * 新增tag记录
     * @param tagId 标签ID
     * @param workId 作品ID
     * @return 是否新增成功
     */
    @Override
    public boolean addNewTagRecord(long tagId, long workId) {
        RecordTagWork recordTagWork = new RecordTagWork();
        recordTagWork.setTagId(tagId).setWorkId(workId);
        return recordTagWorkMapper.insert(recordTagWork) > 0;
    }


    /**
     * 根据workId删除其所有标签记录
     */
    @Override
    public Boolean deleteRecordTagWorkListByWorkId(long workId) {
        QueryWrapper<RecordTagWork> wrapper = new QueryWrapper<>();
        wrapper.eq("work_id", workId);
        return recordTagWorkMapper.delete(wrapper) > 0;
    }
}
