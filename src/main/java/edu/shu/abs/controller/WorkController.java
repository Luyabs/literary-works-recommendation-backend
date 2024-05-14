package edu.shu.abs.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.common.Result;
import edu.shu.abs.entity.Work;
import edu.shu.abs.service.WorkService;
import edu.shu.abs.vo.work.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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
@RequestMapping("/work")
public class WorkController {
    @Autowired
    private WorkService workService;

    @ApiOperation(tags = "文学作品访问", value = "分页访问")
    @GetMapping("/page")
    public Result getPage(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "10") int pageSize, WorkQueryConditionVo condition) {
        IPage<Work> page = workService.getPage(currentPage, pageSize, condition);
        return Result.success().data("page", page);
    }

    @ApiOperation(tags = "文学作品访问", value = "访问作品详细信息")
    @GetMapping("/{workId}")
    public Result getById(@PathVariable long workId) {
        Work work = workService.getOneDetail(workId);
        return Result.success().data("one", work);
    }

    @ApiOperation(tags = "文学作品访问", value = "是否存在该作品(未被逻辑删除)")
    @GetMapping("/exist/{workId}")
    public Result existWork(@PathVariable long workId) {
        Boolean res = workService.existWork(workId);
        return Result.success().data("exist", res);
    }

    @ApiOperation(tags = "文学作品管理", value = "添加作品")
    @PostMapping
    public Result postWork(@RequestBody WorkNewPostVo workNewPostVo) {
        Boolean res = workService.saveWork(workNewPostVo);
        return res ? Result.success().message("文学作品新增成功") : Result.error();
    }

    @ApiOperation(tags = "文学作品管理", value = "修改作品")
    @PutMapping
    public Result editWork(@RequestBody WorkEditVo workEditVo) {
        Boolean res = workService.updateWork(workEditVo);
        return res ? Result.success().message("文学作品修改成功") : Result.error();
    }

    @ApiOperation(tags = "文学作品管理", value = "逻辑删除作品")
    @DeleteMapping("/{workId}")
    public Result deleteWork(@PathVariable long workId) {
        Boolean res = workService.dropWork(workId);
        return res ? Result.success().message("文学作品删除成功") : Result.error();
    }

    @ApiOperation(tags = "文学作品数据统计", value = "分页获取最高评分作品")
    @GetMapping("/highest_rating")
    public Result getHighestRating(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "50") int pageSize) {
        IPage<WorkRatingVo> page = workService.getHighestRating(currentPage, pageSize);
        return Result.success().data("page", page);
    }

    @ApiOperation(tags = "文学作品数据统计", value = "分页获取最多评价作品")
    @GetMapping("/most_rating")
    public Result getMostRating(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "50") int pageSize) {
        IPage<WorkRatingVo> page = workService.getMostRating(currentPage, pageSize);
        return Result.success().data("page", page);
    }

    @ApiOperation(tags = "文学作品数据统计", value = "分页获取被浏览最多作品")
    @GetMapping("/most_visit")
    public Result getMostVisit(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "50") int pageSize) {
        IPage<WorkVisitCountVo> page = workService.getMostVisit(currentPage, pageSize);
        return Result.success().data("page", page);
    }

    @ApiOperation(tags = "文学作品数据统计", value = "分页获取被收藏最多作品")
    @GetMapping("/most_collect")
    public Result getMostCollect(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "50") int pageSize) {
        IPage<WorkCollectCountVo> page = workService.getMostCollect(currentPage, pageSize);
        return Result.success().data("page", page);
    }
}
