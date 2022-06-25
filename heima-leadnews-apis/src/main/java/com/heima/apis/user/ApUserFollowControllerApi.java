package com.heima.apis.user;

import com.heima.model.user.pojos.ApUserFollow;

public interface ApUserFollowControllerApi {

    /**
     * 根据用户id和关注作者的id查询
     * @param userId
     * @param followId
     * @return
     */
    public ApUserFollow findByUserIdAndFollowId(Integer userId,Integer followId);
}
