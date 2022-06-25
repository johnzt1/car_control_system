package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.feign.BehaviorFeign;
import com.heima.article.feign.UserFeign;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;

import com.heima.article.mapper.ApCollectionMapper;
import com.heima.article.mapper.AuthorMapper;
import com.heima.article.service.ArticleInfoService;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;

import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.article.pojos.ApCollection;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.behavior.pojos.ApLikesBehavior;
import com.heima.model.behavior.pojos.ApUnlikesBehavior;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ArticleInfoServiceImpl implements ArticleInfoService {

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Override
    public ResponseResult loadArticleInfo(ArticleInfoDto dto) {

        Map<String, Object> resultMap = new HashMap<>();

        //1.检查参数
        if(dto == null || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.查询文章的配置
        ApArticleConfig apArticleConfig = apArticleConfigMapper.selectOne(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, dto.getArticleId()));
        if(apArticleConfig == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //3.查询文章的内容
        if(!apArticleConfig.getIsDelete()&& !apArticleConfig.getIsDown()){
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getArticleId()));
            resultMap.put("content",apArticleContent);
        }
        resultMap.put("config",apArticleConfig);
        //4.结果返回
        return ResponseResult.okResult(resultMap);
    }

    @Autowired
    private BehaviorFeign behaviorFeign;

    @Autowired
    private ApCollectionMapper apCollectionMapper;

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private AuthorMapper authorMapper;

    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {
        //1.检查参数
        if(dto == null || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询行为实体
        ApUser user = AppThreadLocalUtils.getUser();
        ApBehaviorEntry apBehaviorEntry = behaviorFeign.findByUserIdOrEquipmentId(user.getId(), dto.getEquipmentId());
        if(apBehaviorEntry == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        boolean isUnlike=false,isLike = false,isCollection = false,isFollow = false;

        //3.查询不喜欢行为
        ApUnlikesBehavior apUnlikesBehavior = behaviorFeign.findUnLikeByArticleIdAndEntryId(apBehaviorEntry.getId(), dto.getArticleId());
        if(apUnlikesBehavior != null && apUnlikesBehavior.getType() == ApUnlikesBehavior.Type.UNLIKE.getCode()){
            isUnlike = true;
        }

        //4.查询点赞行为
        ApLikesBehavior apLikesBehavior = behaviorFeign.findLikeByArticleIdAndEntryId(dto.getArticleId(), apBehaviorEntry.getId(), ApLikesBehavior.Type.ARTICLE.getCode());
        if(apLikesBehavior != null && apLikesBehavior.getOperation() == ApLikesBehavior.Operation.LIKE.getCode()){
            isLike = true;
        }

        //5.查询收藏行为
        ApCollection apCollection = apCollectionMapper.selectOne(Wrappers.<ApCollection>lambdaQuery().eq(ApCollection::getEntryId, apBehaviorEntry.getId())
                .eq(ApCollection::getArticleId, dto.getArticleId()).eq(ApCollection::getType, ApCollection.Type.ARTICLE.getCode()));
        if(apCollection != null){
            isCollection = true;
        }

        //6.查询是否关注
        ApAuthor apAuthor = authorMapper.selectById(dto.getAuthorId());
        if(apAuthor != null){
            ApUserFollow apUserFollow = userFeign.findByUserIdAndFollowId(user.getId(), apAuthor.getId());
            if(apUserFollow != null){
                isFollow = true;
            }
        }


        //7.结果返回  {"isfollow":true,"islike":true,"isunlike":false,"iscollection":true}
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("isfollow",isFollow);
        resultMap.put("islike",isLike);
        resultMap.put("isunlike",isUnlike);
        resultMap.put("iscollection",isCollection);
        return ResponseResult.okResult(resultMap);
    }
}
