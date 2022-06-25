package com.heima.search.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.search.pojos.ApUserSearch;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.feign.BehaviorFeign;
import com.heima.search.mapper.ApUserSearchMapper;
import com.heima.search.service.ApUserSearchService;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log4j2
public class ApUserSearchServiceImpl extends ServiceImpl<ApUserSearchMapper, ApUserSearch> implements ApUserSearchService {
    @Override
    public ResponseResult findUserSearch(UserSearchDto userSearchDto) {
        //1.检查数据
        if(userSearchDto.getPageSize() > 50){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.查询行为实体
        ApBehaviorEntry apBehaviorEntry = getEntry(userSearchDto);
        if(apBehaviorEntry == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //3.分页查询，默认查询5条数据返回
        IPage pageParam = new Page(0, userSearchDto.getPageSize());
        IPage page = page(pageParam, Wrappers.<ApUserSearch>lambdaQuery().eq(ApUserSearch::getEntryId, apBehaviorEntry.getId())
                .eq(ApUserSearch::getStatus, 1));

        return ResponseResult.okResult(page.getRecords());
    }


    @Autowired
    private BehaviorFeign behaviorFeign;

    /**
     * 获取行为实体
     * @param userSearchDto
     * @return
     */
    private ApBehaviorEntry getEntry(UserSearchDto userSearchDto) {
        ApUser user = AppThreadLocalUtils.getUser();
        return behaviorFeign.findByUserIdOrEntryId(user.getId(),userSearchDto.getEquipmentId());
    }

    @Override
    @Async("taskExecutor")
    public ResponseResult delUserSearch(UserSearchDto userSearchDto) {
        //1.检查参数
        if(userSearchDto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.更新当前搜索记录的状态  status  0
        ApBehaviorEntry apBehaviorEntry = getEntry(userSearchDto);
        if(apBehaviorEntry == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        update(Wrappers.<ApUserSearch>lambdaUpdate().eq(ApUserSearch::getId,userSearchDto.getId()).eq(ApUserSearch::getEntryId,apBehaviorEntry.getId())
                .set(ApUserSearch::getStatus,0));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    @Async("taskExecutor")
    public void insert(Integer entryId, String searchWords) {
//        int a = 1/0;
        //1.查询当前搜索记录
        ApUserSearch apUserSearch = getOne(Wrappers.<ApUserSearch>lambdaQuery().eq(ApUserSearch::getEntryId, entryId).eq(ApUserSearch::getKeyword, searchWords));

        //2.如果存在 更新状态
        if(apUserSearch != null && apUserSearch.getStatus() == 1){
            log.info("当前关键字已存在，无需再次保存");
            return;
        }else if(apUserSearch != null && apUserSearch.getStatus() == 0){
            apUserSearch.setStatus(1);
            updateById(apUserSearch);
            return;
        }else {
            //3.如果不存在，保存新的数据
            apUserSearch = new ApUserSearch();
            apUserSearch.setEntryId(entryId);
            apUserSearch.setStatus(1);
            apUserSearch.setKeyword(searchWords);
            apUserSearch.setCreatedTime(new Date());
            save(apUserSearch);
        }
    }
}
