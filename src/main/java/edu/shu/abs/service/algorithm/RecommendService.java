package edu.shu.abs.service.algorithm;

import edu.shu.abs.common.authentication.UserInfo;
import edu.shu.abs.common.exception.exception.ServiceException;
import edu.shu.abs.entity.Work;
import edu.shu.abs.service.ReviewUserWorkService;
import edu.shu.abs.service.WorkService;
import edu.shu.abs.vo.algorithm.WorkPredictRatingVo;
import edu.shu.abs.vo.algorithm.recommend.LfmPredictVo;
import edu.shu.abs.vo.algorithm.recommend.LfmRecallQueryVo;
import edu.shu.abs.vo.algorithm.recommend.LfmWorkSimilarQueryVo;
import edu.shu.abs.vo.work.WorkRatingVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RecommendService {
    @Value("${algorithm-backend.url}")
    private String baseUrl;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WorkService workService;

    @Autowired
    private ReviewUserWorkService reviewUserWorkService;

    public List<Double> predict(LfmPredictVo lfmPredictVo) {
        // 请求地址
        String url = baseUrl + "recommend/predict";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("user_ids", Collections.nCopies(lfmPredictVo.getWorkIds().size(), UserInfo.getUserId()));
        params.put("book_ids", lfmPredictVo.getWorkIds());

        // 提交参数设置
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        // 组装请求体 发送请求
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);
        Map response = responseEntity.getBody();
        Object obj = response.get("predictions");
        try {
            return (List<Double>) obj;
        } catch (ClassCastException classCastException) {
            return List.of((Double) obj);
        }
    }

    public List<WorkPredictRatingVo> recall(LfmRecallQueryVo lfmRecallQueryVo) {
        // 请求地址
        String url = baseUrl + "recommend/recall";

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", UserInfo.getUserId());
        int k = lfmRecallQueryVo.getK();
        params.put("k", k + 3); // 避免有些文学作品看过 多搜3本
        params.put("threshold", lfmRecallQueryVo.getThreshold());
        params.put("mix_weight", lfmRecallQueryVo.getMixWeight());

        // 提交参数设置
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        // 组装请求体 发送请求
        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);
        Map response = responseEntity.getBody();
        List<List<Object>> recallList = (List<List<Object>>) response.get("recommend_list");

        // 加入返回列表
        List<WorkPredictRatingVo> res = new ArrayList<>();
        for (List<Object> rec : recallList) {
            Long workId = Long.valueOf(rec.get(0).toString());
            Double ratingHat = (Double) rec.get(1);

            // 如果作品已被当前用户打分 或 已被逻辑删除 则跳过
            if (reviewUserWorkService.existRating(workId, UserInfo.getUserId()) || !workService.existWork(workId)) {
                continue;
            }

            Work work = workService.getByIdNotNull(workId);
            WorkPredictRatingVo workPredictRatingVo = new WorkPredictRatingVo();
            BeanUtils.copyProperties(work, workPredictRatingVo);
            workPredictRatingVo.setRatingHat(ratingHat);
            res.add(workPredictRatingVo);
            if (res.size() >= k)
                break;
        }

        // 如果返回的列表长度不够 则填充热门作品
        if (res.size() < k) {
            List<WorkRatingVo> works = workService.getMostRating(k * 100);
            Random random = new Random();
            while (res.size() < k) {
                int randIndex = Math.min(random.nextInt(k * 100), works.size() - 1);
                // 如果作品已被当前用户打分 或 已被逻辑删除 则跳过
                if (!reviewUserWorkService.existRating(works.get(randIndex).getWorkId(), UserInfo.getUserId())) {
                    WorkPredictRatingVo workPredictRatingVo = new WorkPredictRatingVo();
                    BeanUtils.copyProperties(works.get(randIndex), workPredictRatingVo);
                    if (works.get(randIndex).getAvgRating() != null)
                        workPredictRatingVo.setRatingHat(works.get(randIndex).getAvgRating() * -1.0);   // 填补的是评分的负数
                    else
                        workPredictRatingVo.setRatingHat(-0.000000001);
                    res.add(workPredictRatingVo);
                }
            }
        }
        return res;
    }


    /**
     * 获取与指定作品相似的作品
     */
    public List<WorkPredictRatingVo> getSimilarWork(LfmWorkSimilarQueryVo lfmWorkSimilarQueryVo) {
        final String REDIS_SIMILAR_KEY = "similarWorks:" + lfmWorkSimilarQueryVo;
        int k = lfmWorkSimilarQueryVo.getK();

        // 确保文学作品存在
        workService.getByIdNotNull(lfmWorkSimilarQueryVo.getWorkId());

        List<List<Object>> recallList;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_SIMILAR_KEY))) { // 已在缓存中
            recallList = (List<List<Object>>) redisTemplate.opsForValue().get(REDIS_SIMILAR_KEY);
        }
        else {  // 未在缓存中
            // 请求地址
            String url = baseUrl + "recommend/similar";

            // 请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("book_id", lfmWorkSimilarQueryVo.getWorkId());
            params.put("k", k + 3); // 避免有些文学作品看过 多搜3本

            // 提交参数设置
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

            // 组装请求体 发送请求
            ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, requestEntity, Map.class);
            Map response = responseEntity.getBody();
            recallList = (List<List<Object>>) response.get("recommend_list");

            // 缓存到redis中
            redisTemplate.opsForValue().set(REDIS_SIMILAR_KEY, recallList);
            redisTemplate.expire(REDIS_SIMILAR_KEY, 35, TimeUnit.MINUTES);
        }

        // 加入返回列表
        List<WorkPredictRatingVo> res = new ArrayList<>();
        for (List<Object> rec : recallList) {
            Long workId = Long.valueOf(rec.get(0).toString());
            Double ratingHat = (Double) rec.get(1);

            // 如果作品已被当前用户打分 或 已被逻辑删除 则跳过
            if (reviewUserWorkService.existRating(workId, UserInfo.getUserId()) || !workService.existWork(workId)) {
                continue;
            }

            Work work = workService.getByIdNotNull(workId);
            WorkPredictRatingVo workPredictRatingVo = new WorkPredictRatingVo();
            BeanUtils.copyProperties(work, workPredictRatingVo);
            workPredictRatingVo.setRatingHat(ratingHat);
            res.add(workPredictRatingVo);
            if (res.size() >= k)
                break;
        }

        // 如果返回的列表长度不够 则填充热门作品
        if (res.size() < k) {
            List<WorkRatingVo> works = workService.getMostRating(k * 100);
            Random random = new Random();
            while (res.size() < k) {
                int randIndex = random.nextInt(k * 100);
                // 如果作品已被当前用户打分 或 已被逻辑删除 则跳过
                if (!reviewUserWorkService.existRating(works.get(randIndex).getWorkId(), UserInfo.getUserId())) {
                    WorkPredictRatingVo workPredictRatingVo = new WorkPredictRatingVo();
                    BeanUtils.copyProperties(works.get(randIndex), workPredictRatingVo);
                    if (works.get(randIndex).getAvgRating() != null)
                        workPredictRatingVo.setRatingHat(works.get(randIndex).getAvgRating() * -1.0);   // 填补的是评分的负数
                    else
                        workPredictRatingVo.setRatingHat(-0.000000001);
                    res.add(workPredictRatingVo);
                }
            }
        }
        return res;
    }

//    public boolean initTrain() {
//        if (!UserInfo.isAdmin())
//            throw new NoAccessException("只有管理员才能训练模型");
//        if (isTraining())
//            throw new ServiceException("同一时间只允许一个线程执行 初始化训练/增量训练");
//
//        restTemplate.postForEntity(baseUrl + "recommend/init_train", new HttpEntity<>(null), Map.class);
//        return true;
//    }

//    public boolean updateTrain(String newlyUpdateTime) {
//        if (!UserInfo.isAdmin())
//            throw new NoAccessException("只有管理员才能训练模型");
//        return updateTrainWithoutVerifyAdmin(newlyUpdateTime);
//    }

    public boolean updateTrainWithoutVerifyAdmin(String newlyUpdateTime) {
        if (isTraining())
            throw new ServiceException("同一时间只允许一个线程执行 初始化训练/增量训练");

        // 请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("newly_update_time", newlyUpdateTime);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(params, headers);

        restTemplate.postForEntity(baseUrl + "recommend/update_train", requestEntity, Map.class);
        return true;
    }

//    public TestMetricsVo test() {
//        ResponseEntity<Map> responseEntity = restTemplate.postForEntity(baseUrl + "lfm/test", new HttpEntity<>(null), Map.class);
//        Map response = responseEntity.getBody();
//        TestMetricsVo testMetricsVo = new TestMetricsVo();
//        testMetricsVo.setAvgLoss((Double) response.get("avg_loss"));
//        testMetricsVo.setAvgAccuracy((Double) response.get("avg_accuracy"));
//        return testMetricsVo;
//    }

    private boolean isTraining() {
        Map map = restTemplate.getForEntity(baseUrl + "recommend/is_training", Map.class).getBody();
        return (Boolean) map.get("is_training");
    }

}
