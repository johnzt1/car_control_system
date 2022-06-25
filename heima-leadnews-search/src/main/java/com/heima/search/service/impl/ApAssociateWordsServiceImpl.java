package com.heima.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.search.pojos.ApAssociateWords;
import com.heima.search.mapper.ApAssociateWordsMapper;
import com.heima.search.model.Trie;
import com.heima.search.service.ApAssociateWordsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class ApAssociateWordsServiceImpl extends ServiceImpl<ApAssociateWordsMapper, ApAssociateWords> implements ApAssociateWordsService {
    @Override
    public ResponseResult search(UserSearchDto dto) {
        //1.检查参数
        if (dto.getPageSize() > 50) {
            return ResponseResult.okResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.模糊查询数据
        IPage pageParam = new Page(0, dto.getPageSize());
        List<ApAssociateWords> list = list();
        IPage page = page(pageParam, Wrappers.<ApAssociateWords>lambdaQuery().like(ApAssociateWords::getAssociateWords, dto.getSearchWords()));
        return ResponseResult.okResult(page.getRecords());
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public ResponseResult searchV2(UserSearchDto dto) {
        //1.从缓存中获取数据
        String assoStr = redisTemplate.opsForValue().get("associate_list");
        List<ApAssociateWords> apAssociateWords = null;
        if(StringUtils.isNotEmpty(assoStr)){
            //2.缓存中存在，直接拿数据
            apAssociateWords = JSON.parseArray(assoStr, ApAssociateWords.class);
        }else {
            //3.缓存中不存在，从数据库中获取数据，存储到redis
            apAssociateWords = list();
            redisTemplate.opsForValue().set("associate_list", JSON.toJSONString(apAssociateWords));
        }
        //4.构建trie数据结构，从trie中获取数据，封装返回
        Trie t = new Trie();
        for (ApAssociateWords apAssociateWord : apAssociateWords) {
            t.insert(apAssociateWord.getAssociateWords());
        }

        List<String> ret = t.startWith(dto.getSearchWords());
        List<ApAssociateWords> resultList  = new ArrayList<>();
        for (String s : ret) {
            ApAssociateWords aaw = new ApAssociateWords();
            aaw.setAssociateWords(s);
            resultList.add(aaw);
        }

        return ResponseResult.okResult(resultList);
    }
}
