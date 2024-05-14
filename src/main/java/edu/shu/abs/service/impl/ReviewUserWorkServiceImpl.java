package edu.shu.abs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.common.Page;
import edu.shu.abs.common.authentication.UserInfo;
import edu.shu.abs.common.exception.exception.NoAccessException;
import edu.shu.abs.common.exception.exception.NotExistException;
import edu.shu.abs.entity.ReviewUserWork;
import edu.shu.abs.entity.Work;
import edu.shu.abs.mapper.ReviewUserWorkMapper;
import edu.shu.abs.mapper.WorkMapper;
import edu.shu.abs.service.ReviewUserWorkService;
import edu.shu.abs.common.base.BaseServiceImpl;
import edu.shu.abs.service.UserService;
import edu.shu.abs.service.WorkService;
import edu.shu.abs.vo.review.ReviewNewPostVo;
import edu.shu.abs.vo.review.ReviewQueryConditionVo;
import edu.shu.abs.vo.review.ReviewUserWorkWithUserVo;
import edu.shu.abs.vo.review.ReviewUserWorkWithWorkVo;
import edu.shu.abs.vo.user.UserPrivacyVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@Service
@Slf4j
public class ReviewUserWorkServiceImpl extends BaseServiceImpl<ReviewUserWorkMapper, ReviewUserWork> implements ReviewUserWorkService {
    @Autowired
    private ReviewUserWorkMapper reviewUserWorkMapper;

    @Autowired
    private WorkMapper workMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkService workService;

    @Override
    public Page<ReviewUserWorkWithUserVo> getPage(int currentPage, int pageSize, long workId, ReviewQueryConditionVo condition) {
        workService.proveExistWork(workId);
        QueryWrapper<ReviewUserWork> wrapper = new QueryWrapper<>();
        wrapper.eq("work_id", workId)
                .isNotNull(ObjectUtils.isNotEmpty(condition.getIgnoreNoContent()) && condition.getIgnoreNoContent(), "content")
                .orderByDesc("create_time");

        if (ObjectUtils.isNotEmpty(condition.getRatingUpperBound()))
            wrapper.le("rating", Math.max(Math.min(5, condition.getRatingUpperBound()), 1));
        if (ObjectUtils.isNotEmpty(condition.getRatingLowerBound()))
            wrapper.ge("rating", Math.min(Math.max(1, condition.getRatingLowerBound()), 5));
        return reviewUserWorkMapper.selectReviewUserWorkExtraVoPage(new Page<>(currentPage, pageSize), wrapper);
    }

    @Override
    public IPage<ReviewUserWorkWithWorkVo> getMyPageReview(int currentPage, int pageSize) {
        return reviewUserWorkMapper.selectReviewUserWorkWithWork(new Page<>(currentPage, pageSize), UserInfo.getUserId());
    }

    @Override
    public ReviewUserWork getMyReview(long workId) {
        workService.proveExistWork(workId);
        ReviewUserWork review = reviewUserWorkMapper.selectOne(
                new QueryWrapper<ReviewUserWork>()
                        .eq("work_id", workId)
                        .eq("user_id", UserInfo.getUserId())
        );
        return review;
    }

    @Override
    @Transactional
    public boolean updateReview(ReviewNewPostVo reviewNewPostVo) {
        ReviewUserWork myReview = this.getMyReview(reviewNewPostVo.getWorkId());
        Work work = workMapper.selectById(reviewNewPostVo.getWorkId());
        workService.proveExistWork(work.getWorkId());
        // 还未对该作品发表评论
        if (myReview == null) {
            myReview = new ReviewUserWork().setUserId(UserInfo.getUserId());
            BeanUtils.copyProperties(reviewNewPostVo, myReview);
            if (ObjectUtils.isEmpty(myReview.getContent()))
                myReview.setContent(null);  // 如果评论内容是空或空串则设置为null
            work.setSumRating(work.getSumRating() + reviewNewPostVo.getRating())
                    .setSumRatingUserNumber(work.getSumRatingUserNumber() + 1); // 更新work表的评分数据
            return workMapper.updateById(work) > 0 && reviewUserWorkMapper.insert(myReview) > 0;
        }

        // 对该作品发表过评论 此时修改
        if (reviewNewPostVo.getRating() == null)
            throw new NotExistException("评分不能为空");
        work.setSumRating(work.getSumRating() - myReview.getRating() + reviewNewPostVo.getRating()); // 更新work表的评分数据
        myReview.setRating(reviewNewPostVo.getRating());

        if (reviewNewPostVo.getContent() != null)
            myReview.setContent(reviewNewPostVo.getContent());
        return workMapper.updateById(work) > 0 && reviewUserWorkMapper.updateById(myReview) > 0;
    }

    @Override
    public boolean existRating(long workId, long userId) {
        QueryWrapper<ReviewUserWork> wrapper = new QueryWrapper<>();
        wrapper.eq("work_id", workId)
                .eq("user_id", userId);
        return reviewUserWorkMapper.exists(wrapper);
    }

    @Override
    public IPage<ReviewUserWorkWithWorkVo> getOtherPageReview(int currentPage, int pageSize, long userId) {
        if (userId != UserInfo.getUserId() && !userService.getOtherPrivacySetting(userId).getIsCommentPublic()) {
            throw new NoAccessException("该用户未公开自己的评论列表");
        }
        return reviewUserWorkMapper.selectReviewUserWorkWithWork(new Page<>(currentPage, pageSize), userId);
    }
}
