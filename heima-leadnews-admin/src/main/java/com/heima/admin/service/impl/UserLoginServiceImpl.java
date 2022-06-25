package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.UserLoginService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.groovy.util.Maps;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.sql.Wrapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserLoginServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements UserLoginService {

    @Override
    public ResponseResult login(AdUserDto dto) {
        //1.检查参数
        if (StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "用户名或密码错误");
        }

        //2.查询数据库中的用户信息
        List<AdUser> list = list(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, dto.getName()));
        if(list != null && list.size() ==1){
            AdUser adUser = list.get(0);
            //3.比对密码
            String pswd = DigestUtils.md5DigestAsHex((dto.getPassword()+adUser.getSalt()).getBytes());
            if(adUser.getPassword().equals(pswd)){
                //4.返回数据jwt
                Map<String,Object>  map = new HashMap<>();
                map.put("token",AppJwtUtil.getToken(adUser.getId().longValue()));
                adUser.setPassword("");
                adUser.setSalt("");
                map.put("user",adUser);
                return ResponseResult.okResult(map);
            }else {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
        }else {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
        }

    }
}
