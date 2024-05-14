package edu.shu.abs.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.common.Result;
import edu.shu.abs.entity.ReviewUserWork;
import edu.shu.abs.service.ReviewUserWorkService;
import edu.shu.abs.vo.review.ReviewNewPostVo;
import edu.shu.abs.vo.review.ReviewQueryConditionVo;
import edu.shu.abs.vo.review.ReviewUserWorkWithUserVo;
import edu.shu.abs.vo.review.ReviewUserWorkWithWorkVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@RestController
@RequestMapping("/review")
public class ReviewUserWorkController {
    @Autowired
    private ReviewUserWorkService reviewUserWorkService;

    @ApiOperation(tags = "文学作品评论", value = "分页访问某作品的评论")
    @GetMapping("/{workId}")
    public Result getPage(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "10") int pageSize,
                          @PathVariable long workId, ReviewQueryConditionVo condition) {
        IPage<ReviewUserWorkWithUserVo> page = reviewUserWorkService.getPage(currentPage, pageSize, workId, condition);
        return Result.success().data("page", page);
    }

    @ApiOperation(tags = "文学作品评论", value = "分页获取用户自己的所有评论")
    @GetMapping("/my")
    public Result getMyPageReview(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "10") int pageSize) {
        IPage<ReviewUserWorkWithWorkVo> reviewPage = reviewUserWorkService.getMyPageReview(currentPage, pageSize);
        return Result.success().data("page", reviewPage);
    }

    @ApiOperation(tags = "文学作品评论", value = "获取用户自己对某作品的评论")
    @GetMapping("/my/{workId}")
    public Result getMyReview(@PathVariable long workId) {
        ReviewUserWork review = reviewUserWorkService.getMyReview(workId);
        return Result.success().data("one", review);
    }

    @ApiOperation(tags = "文学作品评论", value = "发表对某作品的评论")
    @PostMapping
    public Result updateMyReview(@RequestBody ReviewNewPostVo review) {
        boolean res = reviewUserWorkService.updateReview(review);
        return res ? Result.success().message("评论发表成功") : Result.error();
    }

    @ApiOperation(tags = "访问他人信息", value = "分页获取某用户的所有评论")
    @GetMapping("/visit/{userId}")
    public Result getOtherPageReview(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "10") int pageSize, @PathVariable long userId) {
        IPage<ReviewUserWorkWithWorkVo> reviewPage = reviewUserWorkService.getOtherPageReview(currentPage, pageSize, userId);
        return Result.success().data("page", reviewPage);
    }
}
