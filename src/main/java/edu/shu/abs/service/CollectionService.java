package edu.shu.abs.service;

import edu.shu.abs.entity.Collection;
import edu.shu.abs.common.base.BaseService;
import edu.shu.abs.vo.collection.CollectionNewPostVo;
import edu.shu.abs.vo.collection.CollectionUpdateVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
public interface CollectionService extends BaseService<Collection> {

    List<Collection> getMyAllCollection();

    boolean saveNewCollection(CollectionNewPostVo collectionNewPostVo);

    boolean updateCollection(CollectionUpdateVo collectionUpdateVo);

    boolean dropCollection(Long collectionId);

    Collection proveOwnerAuthority(Long collectionId);

    List<Collection> getOtherAllCollection(long userId);
}
