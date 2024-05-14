package edu.shu.abs.controller;

import edu.shu.abs.common.Result;
import edu.shu.abs.entity.Collection;
import edu.shu.abs.service.CollectionService;
import edu.shu.abs.service.RecordCollectionWorkService;
import edu.shu.abs.vo.collection.CollectionNewPostVo;
import edu.shu.abs.vo.collection.CollectionUpdateVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author abstraction
 * @since 2024-01-24 11:05:54
 */
@RestController
@RequestMapping("/collection")
public class CollectionController {
    @Autowired
    private CollectionService collectionService;

    @ApiOperation(tags = "收藏夹信息", value = "访问自己的全部收藏夹")
    @GetMapping("/my")
    public Result getMyAllCollection() {
        List<Collection> allCollection = collectionService.getMyAllCollection();
        return Result.success().data("all", allCollection);
    }

    @ApiOperation(tags = "收藏夹信息", value = "创建收藏夹")
    @PostMapping("/my")
    public Result postNewCollection(@RequestBody CollectionNewPostVo collectionNewPostVo) {
        boolean res = collectionService.saveNewCollection(collectionNewPostVo);
        return res ? Result.success().message("收藏夹创建成功") : Result.error();
    }

    @ApiOperation(tags = "收藏夹信息", value = "修改收藏夹信息")
    @PutMapping("/my")
    public Result updateCollection(@RequestBody CollectionUpdateVo collectionUpdateVo) {
        boolean res = collectionService.updateCollection(collectionUpdateVo);
        return res ? Result.success().message("收藏夹更新成功") : Result.error();
    }

    @ApiOperation(tags = "收藏夹信息", value = "删除收藏夹")
    @DeleteMapping("/my/{collectionId}")
    public Result dropCollection(@PathVariable Long collectionId) {
        boolean res = collectionService.dropCollection(collectionId);
        return res ? Result.success().message("收藏夹删除成功") : Result.error();
    }

    @ApiOperation(tags = "访问他人信息", value = "访问他人的全部公开收藏夹")
    @GetMapping("/visit/{userId}")
    public Result getMyAllCollection(@PathVariable long userId) {
        List<Collection> allCollection = collectionService.getOtherAllCollection(userId);
        return Result.success().data("all", allCollection);
    }
}
