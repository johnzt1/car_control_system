package com.heima.apis.user;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;

public interface UserRelationControllerApi {

    /**
     * 关注或取消关注
     * @param dto
     * @return
     */
    public ResponseResult follow(UserRelationDto dto);
}
