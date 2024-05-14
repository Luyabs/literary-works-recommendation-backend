package edu.shu.abs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.common.Page;
import edu.shu.abs.common.authentication.UserInfo;
import edu.shu.abs.common.exception.exception.NotExistException;
import edu.shu.abs.entity.HistoryUserWork;
import edu.shu.abs.mapper.HistoryUserWorkMapper;
import edu.shu.abs.mapper.WorkMapper;
import edu.shu.abs.service.HistoryUserWorkService;
import edu.shu.abs.common.base.BaseServiceImpl;
import edu.shu.abs.vo.history.HistoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Service
public class HistoryUserWorkServiceImpl extends BaseServiceImpl<HistoryUserWorkMapper, HistoryUserWork> implements HistoryUserWorkService {
    @Autowired
    private HistoryUserWorkMapper historyUserWorkMapper;

    @Autowired
    private WorkMapper workMapper;

    @Override
    public IPage<HistoryVo> getPage(int currentPage, int pageSize) {
        long userId = UserInfo.getUserId();
        Page<HistoryVo> page = new Page<>(currentPage, pageSize);
        return workMapper.selectHistoryUserWorkPage(page, userId);
    }

    @Override
    public boolean updateHistory(long workId) {
        if (workMapper.selectById(workId) == null) {
            throw new NotExistException(workId, "作品编号");
        }
        HistoryUserWork history = historyUserWorkMapper.selectOne(
                new QueryWrapper<HistoryUserWork>()
                        .eq("work_id", workId)
                        .eq("user_id", UserInfo.getUserId())
        );
        if (history == null) {
            history = new HistoryUserWork()
                    .setUserId(UserInfo.getUserId())
                    .setWorkId(workId);
            return historyUserWorkMapper.insert(history) > 0;
        }
        history.setVisitCount(history.getVisitCount() + 1);
        return historyUserWorkMapper.updateById(history) > 0;
    }
}
