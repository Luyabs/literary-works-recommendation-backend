package edu.shu.abs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.common.Page;
import edu.shu.abs.common.authentication.UserInfo;
import edu.shu.abs.common.exception.exception.NoAccessException;
import edu.shu.abs.common.exception.exception.NotExistException;
import edu.shu.abs.common.exception.exception.ServiceException;
import edu.shu.abs.entity.Collection;
import edu.shu.abs.entity.RecordCollectionWork;
import edu.shu.abs.entity.Work;
import edu.shu.abs.mapper.CollectionMapper;
import edu.shu.abs.mapper.RecordCollectionWorkMapper;
import edu.shu.abs.service.CollectionService;
import edu.shu.abs.service.RecordCollectionWorkService;
import edu.shu.abs.common.base.BaseServiceImpl;
import edu.shu.abs.service.WorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Service
public class RecordCollectionWorkServiceImpl extends BaseServiceImpl<RecordCollectionWorkMapper, RecordCollectionWork> implements RecordCollectionWorkService {
    @Autowired
    private RecordCollectionWorkMapper recordCollectionWorkMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private WorkService workService;

    @Override
    public Page<Work> getMyRecordPage(int currentPage, int pageSize, Long collectionId) {
        collectionService.proveOwnerAuthority(collectionId); // 保证收藏夹是自己的
        return recordCollectionWorkMapper.selectWorkPage(new Page<>(currentPage, pageSize), collectionId);
    }

    @Override
    public List<Map<String, Object>> getMyAllCollectionWithCheckingWork(Long workId) {
        workService.proveExistWork(workId); // 保证work存在
        List<Collection> collections = collectionService.getMyAllCollection();
        List<Map<String, Object>> res = new ArrayList<>();
        for (Collection collection : collections) {
            Map<String, Object> map = new HashMap<>();
            map.put("collection", collection);
            map.put("isInCollection", isInCollection(collection.getCollectionId(), workId));
            res.add(map);
        }
        return res;
    }

    @Override
    public boolean saveNewRecord(Long collectionId, Long workId) {
        collectionService.proveOwnerAuthority(collectionId); // 保证收藏夹是自己的
        workService.proveExistWork(workId); // 保证work存在
        if (isInCollection(collectionId, workId))
            throw new ServiceException("该作品已在收藏夹中");
        RecordCollectionWork record = new RecordCollectionWork();
        record.setCollectionId(collectionId)
                .setWorkId(workId);
        return recordCollectionWorkMapper.insert(record) > 0;
    }

    @Override
    public boolean saveNewRecordIntoDefault(Long workId) {
        Collection defaultCollection = getDefaultCollection();
        return saveNewRecord(defaultCollection.getCollectionId(), workId);
    }

    @Override
    public boolean dropRecord(Long collectionId, Long workId) {
        collectionService.proveOwnerAuthority(collectionId); // 保证收藏夹是自己的
        workService.proveExistWork(workId); // 保证work存在
        if (!isInCollection(collectionId, workId))
            throw new ServiceException("该作品未在收藏夹中");
        QueryWrapper<RecordCollectionWork> wrapper = new QueryWrapper<>();
        wrapper.eq("collection_id", collectionId)
                .eq("work_id", workId);
        return recordCollectionWorkMapper.delete(wrapper) > 0;
    }

    @Override
    public IPage<Work> getOtherRecordPage(int currentPage, int pageSize, long collectionId) {
        Collection collection = collectionService.getByIdNotNull(collectionId);
        if (collection.getOwnerId().equals(UserInfo.getUserId()))
            return getMyRecordPage(currentPage, pageSize, collectionId);
        if (!collection.getIsPublic())
            throw new NoAccessException("该收藏夹未公开");
        return recordCollectionWorkMapper.selectWorkPage(new Page<>(currentPage, pageSize), collectionId);

    }

    /**
     * 获取默认收藏夹 (如果没有默认收藏夹，将会抛出异常)
     */
    private Collection getDefaultCollection() {
        QueryWrapper<Collection> wrapper = new QueryWrapper<>();
        wrapper.eq("owner_id", UserInfo.getUserId())
                .eq("is_default_collection", true);
        Collection collection = collectionMapper.selectOne(wrapper);
        if (collection == null)
            throw new NotExistException("还未设置默认收藏夹");
        return collection;
    }

    /**
     * 判断作品是否在收藏夹中
     */
    private Boolean isInCollection(Long collectionId, Long workId) {
        QueryWrapper<RecordCollectionWork> wrapper = new QueryWrapper<>();
        wrapper.eq("collection_id", collectionId)
                .eq("work_id", workId);
        return recordCollectionWorkMapper.exists(wrapper);
    }
}
