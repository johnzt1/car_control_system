package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface WmUserControllerApi {

    /**
     * 保存自媒体用户
     * @param wmUser
     * @return
     */
    public ResponseResult save(WmUser wmUser);

    /**
     * 根据名称查询自媒体用户
     * @param name
     * @return
     */
    public WmUser findByName(String name);

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    public WmUser findWmUserById(Integer id);

}
