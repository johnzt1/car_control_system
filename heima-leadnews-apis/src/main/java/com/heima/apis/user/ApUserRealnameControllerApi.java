package com.heima.apis.user;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;

public interface ApUserRealnameControllerApi {

    /**
     * 按照状态分页查询用户列表
     * @param dto
     * @return
     */
    public ResponseResult loadListByStatus(AuthDto dto);

    /**
     * 审核通过
     * @param dto
     * @return
     */
    public ResponseResult authPass(AuthDto dto);

    /**
     * 审核失败
     * @param dto
     * @return
     */
    public ResponseResult authFail(AuthDto dto);

}
