package edu.shu.abs.service;

import edu.shu.abs.common.base.BaseService;
import edu.shu.abs.entity.RecordTagWork;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author abstraction
 */
public interface RecordTagWorkService extends BaseService<RecordTagWork> {

    boolean addNewTagRecord(long tagId, long workId);

    Boolean deleteRecordTagWorkListByWorkId(long workId);
}
