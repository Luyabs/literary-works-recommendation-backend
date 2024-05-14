package edu.shu.abs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.common.Page;
import edu.shu.abs.entity.Collection;
import edu.shu.abs.entity.RecordCollectionWork;
import edu.shu.abs.common.base.BaseService;
import edu.shu.abs.entity.Work;
import edu.shu.abs.vo.review.ReviewUserWorkWithWorkVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
public interface RecordCollectionWorkService extends BaseService<RecordCollectionWork> {

    Page<Work> getMyRecordPage(int currentPage, int pageSize, Long collectionId);

    List<Map<String, Object>> getMyAllCollectionWithCheckingWork(Long workId);

    boolean saveNewRecord(Long collectionId, Long workId);

    boolean saveNewRecordIntoDefault(Long workId);

    boolean dropRecord(Long collectionId, Long workId);

    IPage<Work> getOtherRecordPage(int currentPage, int pageSize, long collectionId);
}
