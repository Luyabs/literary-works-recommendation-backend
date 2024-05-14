package edu.shu.abs.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.shu.abs.common.Page;
import edu.shu.abs.common.Result;
import edu.shu.abs.entity.Collection;
import edu.shu.abs.entity.Work;
import edu.shu.abs.service.RecordCollectionWorkService;
import edu.shu.abs.vo.review.ReviewUserWorkWithWorkVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@RestController
@RequestMapping("/record_collection")
public class RecordCollectionWorkController {
    @Autowired
    private RecordCollectionWorkService recordCollectionWorkService;

    @ApiOperation(tags = "收藏夹记录", value = "分页访问自己的某个收藏夹的条目")
    @GetMapping("/{collectionId}")
    public Result getPageCollectionRecord(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "10") int pageSize, @PathVariable Long collectionId) {
        Page<Work> page = recordCollectionWorkService.getMyRecordPage(currentPage, pageSize, collectionId);
        return Result.success().data("page", page);
    }

    @ApiOperation(tags = "收藏夹记录", value = "查询自己的所有收藏夹，同时显示该作品是否在收藏夹中")
    @GetMapping("/with_work/{workId}")
    public Result getMyAllCollectionWithCheckingWork(@PathVariable Long workId) {
        List<Map<String, Object>> all = recordCollectionWorkService.getMyAllCollectionWithCheckingWork(workId);
        return Result.success().data("all", all);
    }

    @ApiOperation(tags = "收藏夹记录", value = "收藏")
    @PostMapping("/{collectionId}/{workId}")
    public Result postNewRecord(@PathVariable Long collectionId, @PathVariable Long workId) {
        boolean res = recordCollectionWorkService.saveNewRecord(collectionId, workId);
        return res ? Result.success().message("收藏成功") : Result.error();
    }

    @ApiOperation(tags = "收藏夹记录", value = "收藏到默认收藏夹")
    @PostMapping("/default/{workId}")
    public Result postNewRecordIntoDefault(@PathVariable Long workId) {
        boolean res = recordCollectionWorkService.saveNewRecordIntoDefault(workId);
        return res ? Result.success().message("收藏成功") : Result.error();
    }

    @ApiOperation(tags = "收藏夹记录", value = "移除收藏")
    @DeleteMapping("/{collectionId}/{workId}")
    public Result dropRecord(@PathVariable Long collectionId, @PathVariable Long workId) {
        boolean res = recordCollectionWorkService.dropRecord(collectionId, workId);
        return res ? Result.success().message("移除收藏成功") : Result.error();
    }


    @ApiOperation(tags = "访问他人信息", value = "分页获取某公开收藏夹的条目")
    @GetMapping("/visit/{collectionId}")
    public Result getOtherPageReview(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "10") int pageSize, @PathVariable long collectionId) {
        IPage<Work> page = recordCollectionWorkService.getOtherRecordPage(currentPage, pageSize, collectionId);
        return Result.success().data("page", page);
    }
}
