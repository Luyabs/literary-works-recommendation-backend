package edu.shu.abs.service;

import edu.shu.abs.common.base.BaseService;
import edu.shu.abs.entity.Collection;
import edu.shu.abs.entity.Tag;
import edu.shu.abs.vo.collection.CollectionNewPostVo;
import edu.shu.abs.vo.collection.CollectionUpdateVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author abstraction
 */
public interface TagService extends BaseService<Tag> {

    Long saveIfNotExist(String tagName);
}
