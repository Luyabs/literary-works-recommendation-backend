package edu.shu.abs.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.entity.Work;
import edu.shu.abs.common.base.BaseService;
import edu.shu.abs.vo.work.*;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
public interface WorkService extends BaseService<Work> {

    IPage<Work> getPage(int currentPage, int pageSize, WorkQueryConditionVo condition);

    Work getOneDetail(long workId);

    Boolean existWork(long workId);

    void proveExistWork(long workId);

    Boolean saveWork(WorkNewPostVo workNewPostVo);

    Boolean updateWork(WorkEditVo workEditVo);

    Boolean dropWork(long workId);

    IPage<WorkRatingVo> getHighestRating(int currentPage, int pageSize);

    IPage<WorkRatingVo> getMostRating(int currentPage, int pageSize);

    List<WorkRatingVo> getMostRating(int num);

    IPage<WorkVisitCountVo> getMostVisit(int currentPage, int pageSize);

    IPage<WorkCollectCountVo> getMostCollect(int currentPage, int pageSize);
}
