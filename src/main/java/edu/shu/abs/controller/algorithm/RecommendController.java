package edu.shu.abs.controller.algorithm;

import edu.shu.abs.common.Result;
import edu.shu.abs.service.algorithm.RecommendService;
import edu.shu.abs.vo.algorithm.WorkPredictRatingVo;
import edu.shu.abs.vo.algorithm.recommend.LfmPredictVo;
import edu.shu.abs.vo.algorithm.recommend.LfmRecallQueryVo;
import edu.shu.abs.vo.algorithm.recommend.LfmWorkSimilarQueryVo;
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
 * @since 2024-04-08 11:05:54
 */
@RestController
@RequestMapping("/recommend")
public class RecommendController {
    @Autowired
    private RecommendService recommendService;

    @ApiOperation(tags = "个性化推荐", value = "预测")
    @PostMapping("/predict")
    public Result predict(@RequestBody LfmPredictVo lfmPredictVo) {
        List<Double> ratings = recommendService.predict(lfmPredictVo);
        return Result.success().data("ratings", ratings);
    }

    @ApiOperation(tags = "个性化推荐", value = "召回")
    @PostMapping("/recall")
    public Result recall(@RequestBody LfmRecallQueryVo lfmRecallQueryVo) {
        List<WorkPredictRatingVo> works = recommendService.recall(lfmRecallQueryVo);
        return Result.success().data("works", works);
    }

    @ApiOperation(tags = "个性化推荐", value = "获取相似物品")
    @PostMapping("/similar")
    public Result getSimilarWork(@RequestBody LfmWorkSimilarQueryVo lfmWorkSimilarQueryVo) {
        List<WorkPredictRatingVo> works = recommendService.getSimilarWork(lfmWorkSimilarQueryVo);
        return Result.success().data("works", works);
    }

//    @ApiOperation(tags = "LFM推荐", value = "初始训练")
//    @PostMapping("/init_train")
//    public Result initTrain() {
//        boolean res = lfmService.initTrain();
//        return res ? Result.success().message("初始训练成功") : Result.error();
//    }
//
//    @ApiOperation(tags = "LFM推荐", value = "增量训练")
//    @PostMapping("/update_train")
//    public Result updateTrain(@RequestParam String newlyUpdateTime) {
//        boolean res = lfmService.updateTrain(newlyUpdateTime);
//        return res ? Result.success().message("增量训练成功") : Result.error();
//    }
//
//    @ApiOperation(tags = "LFM推荐", value = "测试")
//    @PostMapping("/test/dataset")
//    public Result test() {
//        TestMetricsVo testMetricsVo = lfmService.test();
//        return Result.success().data("test_metric", testMetricsVo);
//    }
}
