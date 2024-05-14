package edu.shu.abs.vo.algorithm.recommend;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class LfmPredictVo {
    /**
     * 作品列表
     */
    private List<Integer> workIds;
}
