package edu.shu.abs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.common.Page;
import edu.shu.abs.common.authentication.UserInfo;
import edu.shu.abs.common.base.BaseServiceImpl;
import edu.shu.abs.common.exception.exception.NoAccessException;
import edu.shu.abs.common.exception.exception.NotExistException;
import edu.shu.abs.common.exception.exception.ServiceException;
import edu.shu.abs.entity.RecordCollectionWork;
import edu.shu.abs.entity.Work;
import edu.shu.abs.mapper.RecordCollectionWorkMapper;
import edu.shu.abs.mapper.WorkMapper;
import edu.shu.abs.service.RecordTagWorkService;
import edu.shu.abs.service.TagService;
import edu.shu.abs.service.WorkService;
import edu.shu.abs.vo.work.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.InetAddress;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Service
public class WorkServiceImpl extends BaseServiceImpl<WorkMapper, Work> implements WorkService {
    @Value("${server.port}")
    private int port;

    @Autowired
    private WorkMapper workMapper;

    @Autowired
    private RecordCollectionWorkMapper recordCollectionWorkMapper;

    @Autowired
    private RecordTagWorkService recordTagWorkService;

    @Autowired
    private TagService tagService;

    @Override
    public IPage<Work> getPage(int currentPage, int pageSize, WorkQueryConditionVo condition) {
        QueryWrapper<Work> wrapper = new QueryWrapper<>();
        wrapper.like(ObjectUtils.isNotEmpty(condition.getWorkName()), "work_name", condition.getWorkName())
                .like(ObjectUtils.isNotEmpty(condition.getAuthor()), "author", condition.getAuthor())
                .like(ObjectUtils.isNotEmpty(condition.getPublisher()), "publisher", condition.getPublisher())
                .like(ObjectUtils.isNotEmpty(condition.getIntroduction()), "introduction", condition.getIntroduction())
                .eq("is_deleted", false);
        if (ObjectUtils.isNotEmpty(condition.getTags())) {  // 对tags按空格切分进行多标签查询
            String[] tags = condition.getTags().trim().split("\\s+");
            for (String tag : tags) {
                wrapper.like("tags", tag);
            }
        }
        Page<Work> page = new Page<>(currentPage, pageSize);
        return workMapper.selectPage(page, wrapper);
    }

    @SneakyThrows
    @Override
    public Work getOneDetail(long workId) {
        Work work = workMapper.selectById(workId);
        if (!existWork(workId))
            throw new NotExistException("不存在id=" + workId + "的作品");
        if (work.getCoverLink() == null) {   // 默认图片
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            work.setCoverLink("http://" + ipAddress + ":" + port + "/no_pic.png");
        }
        return work;
    }

    @Override
    @Transactional
    public Boolean saveWork(WorkNewPostVo workNewPostVo) {
        if (!UserInfo.isAdmin())
            throw new NoAccessException("只有管理员才能使用此接口");

        if (ObjectUtils.isEmpty(workNewPostVo.getWorkName()))
            throw new ServiceException("作品名不能为空");
        Work work = new Work();
        BeanUtils.copyProperties(workNewPostVo, work);
        if (work.getTags() != null)
            work.setTags(work.getTags().trim().replaceAll("\\s+", " "));
        boolean res = workMapper.insert(work) > 0;

        String[] tagsArray = work.getTags().trim().split("\\s+");
        if (tagsArray.length > 8)
            throw new ServiceException("最大可以给作品打8个标签");
        for (String tagName : tagsArray) {
            Long tagId = tagService.saveIfNotExist(tagName);
            res &= recordTagWorkService.addNewTagRecord(tagId, work.getWorkId());
        }
        return res;
    }

    @Override
    @Transactional
    public Boolean updateWork(WorkEditVo workEditVo) {
        if (!UserInfo.isAdmin())
            throw new NoAccessException("只有管理员才能使用此接口");

        if (workEditVo.getWorkId() == null)
            throw new ServiceException("作品id不能为空");
        proveExistWork(workEditVo.getWorkId());
        if (ObjectUtils.isEmpty(workEditVo.getWorkName()))
            throw new ServiceException("作品名不能为空");

        Work work = getByIdNotNull(workEditVo.getWorkId());
        BeanUtils.copyProperties(workEditVo, work);
        if (work.getTags() != null)
            work.setTags(work.getTags().trim().replaceAll("\\s+", " "));
        boolean res = workMapper.updateById(work) > 0;

        res &= recordTagWorkService.deleteRecordTagWorkListByWorkId(work.getWorkId()); // 删除旧的标签记录

        String[] tagsArray = work.getTags().trim().split("\\s+");
        if (tagsArray.length > 8)
            throw new ServiceException("最大可以给作品打8个标签");
        for (String tagName : tagsArray) {
            Long tagId = tagService.saveIfNotExist(tagName);
            res &= recordTagWorkService.addNewTagRecord(tagId, work.getWorkId());
        }
        return res;
    }

    /**
     * 删除作品只是逻辑删除<br/>
     * 不需要清除评价关系、访问关系与作品标签关系<br/>
     * 但需要清除收藏关系
     */
    @Override
    @Transactional
    public Boolean dropWork(long workId) {
        if (!UserInfo.isAdmin())
            throw new NoAccessException("只有管理员才能使用此接口");

        Work work = getByIdNotNull(workId);
        if (work.getIsDeleted()) {
            throw new ServiceException("不能重复对同一作品进行逻辑删除");
        }

        work.setIsDeleted(true);
        QueryWrapper<RecordCollectionWork> recordWrapper = new QueryWrapper<>();
        recordWrapper.eq("work_id", workId);
        recordCollectionWorkMapper.delete(recordWrapper);
        return workMapper.updateById(work) > 0;
    }

    @Override
    public IPage<WorkRatingVo> getHighestRating(int currentPage, int pageSize) {
        return workMapper.selectHighestRating(new Page<>(currentPage, pageSize));
    }

    @Override
    public IPage<WorkRatingVo> getMostRating(int currentPage, int pageSize) {
        return workMapper.selectMostRatingPage(new Page<>(currentPage, pageSize));
    }

    @Override
    public List<WorkRatingVo> getMostRating(int num) {
        return workMapper.selectMostRatingLimitNum(num);
    }

    @Override
    public IPage<WorkVisitCountVo> getMostVisit(int currentPage, int pageSize) {
        return workMapper.selectMostVisit(new Page<>(currentPage, pageSize));
    }

    @Override
    public IPage<WorkCollectCountVo> getMostCollect(int currentPage, int pageSize) {
        return workMapper.selectMostCollect(new Page<>(currentPage, pageSize));
    }

    /**
     * 返回作品是否存在
     */
    @Override
    public Boolean existWork(long workId) {
        Work work = workMapper.selectById(workId);
        return work != null && !work.getIsDeleted();
    }

    /**
     * 保证文学作品存在
     */
    @Override
    public void proveExistWork(long workId) {
        if (!existWork(workId))
            throw new NotExistException(workId, "文学作品");
    }
}
