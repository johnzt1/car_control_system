package com.heima.wemedia.controller.v1;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.wemedia.WmUserControllerApi;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
public class WmUserController implements WmUserControllerApi {

    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/save")
    @Override
    public ResponseResult save(@RequestBody WmUser wmUser) {
        wmUserService.save(wmUser);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @GetMapping("/findByName/{name}")
    @Override
    public WmUser findByName(@PathVariable("name") String name) {
        List<WmUser> list = wmUserService.list(Wrappers.<WmUser>lambdaQuery().eq(WmUser::getName, name));
        if(list != null && !list.isEmpty()){
            return list.get(0);
        }
        return null;
    }

    @GetMapping("/findOne/{id}")
    @Override
    public WmUser findWmUserById(@PathVariable("id") Integer id) {
        return wmUserService.getById(id);
    }
}
