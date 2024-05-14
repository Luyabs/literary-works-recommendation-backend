package edu.shu.abs.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import edu.shu.abs.common.authentication.UserInfo;
import edu.shu.abs.common.exception.exception.NoAccessException;
import edu.shu.abs.common.exception.exception.NotExistException;
import edu.shu.abs.entity.Collection;
import edu.shu.abs.entity.RecordCollectionWork;
import edu.shu.abs.mapper.CollectionMapper;
import edu.shu.abs.mapper.RecordCollectionWorkMapper;
import edu.shu.abs.service.CollectionService;
import edu.shu.abs.common.base.BaseServiceImpl;
import edu.shu.abs.vo.collection.CollectionNewPostVo;
import edu.shu.abs.vo.collection.CollectionUpdateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class CollectionServiceImpl extends BaseServiceImpl<CollectionMapper, Collection> implements CollectionService {
    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private RecordCollectionWorkMapper recordCollectionWorkMapper;

    @Override
    public List<Collection> getMyAllCollection() {
        return collectionMapper.selectList(new QueryWrapper<Collection>().eq("owner_id", UserInfo.getUserId()));
    }

    @Override
    public boolean saveNewCollection(CollectionNewPostVo collectionNewPostVo) {
        Collection collection = new Collection().setOwnerId(UserInfo.getUserId());
        BeanUtils.copyProperties(collectionNewPostVo, collection);
        if (collectionNewPostVo.getIsDefaultCollection())
            resetDefaultCollection();
        return collectionMapper.insert(collection) > 0;
    }

    @Override
    public boolean updateCollection(CollectionUpdateVo collectionUpdateVo) {
        Collection collection = proveOwnerAuthority(collectionUpdateVo.getCollectionId());   // 保证收藏夹是自己的
        BeanUtils.copyProperties(collectionUpdateVo, collection);
        if (collectionUpdateVo.getIsDefaultCollection())
            resetDefaultCollection();
        return collectionMapper.updateById(collection) > 0;
    }

    @Override
    @Transactional
    public boolean dropCollection(Long collectionId) {
        proveOwnerAuthority(collectionId);  // 保证收藏夹是自己的
        QueryWrapper<RecordCollectionWork> wrapper = new QueryWrapper<>();
        wrapper.eq("collection_id", collectionId);
        recordCollectionWorkMapper.delete(wrapper); // 逻辑外键
        return collectionMapper.deleteById(collectionId) > 0;
    }

    @Override
    public List<Collection> getOtherAllCollection(long userId) {
        if (userId == UserInfo.getUserId())
            return getMyAllCollection();
        QueryWrapper<Collection> wrapper = new QueryWrapper<Collection>();
        wrapper.eq("owner_id", userId)
                .eq("is_public", true);
        return collectionMapper.selectList(wrapper);
    }

    /**
     * 确保该id的作品属于当前用户
     */
    public Collection proveOwnerAuthority(Long collectionId) {
        Collection collection = collectionMapper.selectById(collectionId);
        if (collection == null)
            throw new NotExistException(collectionId, "收藏夹");
        if (!collection.getOwnerId().equals(UserInfo.getUserId()))
            throw new NoAccessException("无权修改别人的文件夹信息");
        return collection;
    }

    /**
     * 将当前用户的默认收藏夹重置为非默认
     */
    private Boolean resetDefaultCollection() {
        return collectionMapper.resetDefaultCollection(UserInfo.getUserId()) > 0;
    }
}
