package com.heima.apis.behavior;

import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.common.dtos.ResponseResult;

public interface ApLikesBehaviorControllerApi {

    /**
     * 点赞或取消点赞
     * @return
     */
    public ResponseResult like(LikesBehaviorDto dto);

    /**
     * 根据行为实体id和文章id查询点赞行为
     * @return
     */
    public ApLikesBehavior findLikeByArticleIdAndEntryId(Long articleId, Integer entryId, Short type);
}
