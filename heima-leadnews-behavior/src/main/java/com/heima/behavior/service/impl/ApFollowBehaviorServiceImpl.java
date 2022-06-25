package com.heima.behavior.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.behavior.mapper.ApFollowBehaviorMapper;
import com.heima.behavior.service.ApBehaviorEntryService;
import com.heima.behavior.service.ApFollowBehaviorService;
import com.heima.model.behavior.dtos.FollowBehaviorDto;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApFollowBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
public class ApFollowBehaviorServiceImpl extends ServiceImpl<ApFollowBehaviorMapper, ApFollowBehavior> implements ApFollowBehaviorService {

    @Autowired
    private ApBehaviorEntryService apBehaviorEntryService;

    @Override
    public ResponseResult saveFollowBehavior(FollowBehaviorDto dto) {
        //1.查询行为实体
        ApBehaviorEntry apBehaviorEntry = apBehaviorEntryService.findByUserIdOrEquipmentId(dto.getUserId(), null);
        if(apBehaviorEntry == null){
            log.error("行为实体为空");
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.保存关注行为
        ApFollowBehavior apFollowBehavior = new ApFollowBehavior();
        apFollowBehavior.setEntryId(apBehaviorEntry.getId());
        apFollowBehavior.setFollowId(dto.getFollowId());
        apFollowBehavior.setArticleId(dto.getArticleId());
        apFollowBehavior.setCreatedTime(new Date());
        save(apFollowBehavior);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
