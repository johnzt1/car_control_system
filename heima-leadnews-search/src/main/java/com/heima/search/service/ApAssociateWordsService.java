package com.heima.search.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.search.pojos.ApAssociateWords;

public interface ApAssociateWordsService extends IService<ApAssociateWords> {

    /**
     * 联想词查询
     * @param dto
     * @return
     */
    public ResponseResult search(UserSearchDto dto);

    /**
     * 联想词查询
     * @param dto
     * @return
     */
    public ResponseResult searchV2(UserSearchDto dto);
}
