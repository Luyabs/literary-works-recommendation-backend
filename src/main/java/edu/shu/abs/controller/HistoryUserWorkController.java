package edu.shu.abs.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.shu.abs.common.Result;
import edu.shu.abs.entity.Work;
import edu.shu.abs.service.HistoryUserWorkService;
import edu.shu.abs.vo.history.HistoryVo;
import edu.shu.abs.vo.work.WorkQueryConditionVo;
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
@RequestMapping("/history")
public class HistoryUserWorkController {
    @Autowired
    private HistoryUserWorkService historyUserWorkService;

    @ApiOperation(tags = "历史记录", value = "历史记录查询")
    @GetMapping("/page")
    public Result getPage(@RequestParam(defaultValue = "1") int currentPage, @RequestParam(defaultValue = "10") int pageSize) {
        IPage<HistoryVo> page = historyUserWorkService.getPage(currentPage, pageSize);
        return Result.success().data("page", page);
    }

    @ApiOperation(tags = "历史记录", value = "新增历史记录")
    @PostMapping("/{workId}")
    public Result addHistory(@PathVariable long workId) {
        boolean res = historyUserWorkService.updateHistory(workId);
        return res ? Result.success().message("作品访问记录新增成功") : Result.error();
    }
}
