package com.heima.apis.admin;

import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.common.dtos.ResponseResult;

public interface LoginControllerApi {

    /**
     * admin登录功能
     * @param dto
     * @return
     */
    public ResponseResult login(AdUserDto dto);
}
