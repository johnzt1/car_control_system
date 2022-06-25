package com.heima.comment.service.impl;

import com.heima.comment.feign.UserFeign;
import com.heima.comment.service.CommentService;
import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.comment.pojos.ApComment;
import com.heima.model.comment.pojos.ApCommentLike;
import com.heima.model.comment.vo.ApCommentVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CommentServiceImpl implements CommentService {

    @Autowired
    private UserFeign userFeign;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public ResponseResult saveComment(CommentSaveDto dto) {
        //1.检查参数
        if (dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        if (dto.getContent() != null && dto.getContent().length() > 140) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "评论内容不能超过140字");
        }

        //2.判断是否登录
        ApUser user = AppThreadLocalUtils.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //3.安全过滤,自行实现

        //4.保存评论
        ApUser apUser = userFeign.findUserById(user.getId());
        if (apUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE, "当前登录信息有误");
        }
        ApComment apComment = new ApComment();
        apComment.setAuthorId(apUser.getId());
        apComment.setAuthorName(apUser.getName());
        apComment.setContent(dto.getContent());
        apComment.setEntryId(dto.getArticleId());
        apComment.setCreatedTime(new Date());
        apComment.setUpdatedTime(new Date());
        apComment.setImage(apUser.getImage());
        apComment.setLikes(0);
        apComment.setReply(0);
        apComment.setType((short) 0);
        apComment.setFlag((short) 0);
        mongoTemplate.insert(apComment);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult like(CommentLikeDto dto) {
        //1.检查参数
        if (dto.getCommentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        //2.判断是否登录
        ApUser user = AppThreadLocalUtils.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //3.点赞操作
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        if (apComment != null && dto.getOperation() == 0) {
            //更新评论的点赞数量
            apComment.setLikes(apComment.getLikes() + 1);
            mongoTemplate.save(apComment);

            //保存 APP评论信息点赞
            ApCommentLike apCommentLike = new ApCommentLike();
            apCommentLike.setAuthorId(user.getId());
            apCommentLike.setCommentId(apComment.getId());
            apCommentLike.setOperation(dto.getOperation());
            mongoTemplate.save(apCommentLike);
        } else if (apComment != null && dto.getOperation() == 1) {
            //4.取消点赞
            //更新评论的点赞数量
            apComment.setLikes(apComment.getLikes() < 0 ? 0 : apComment.getLikes() - 1);
            mongoTemplate.save(apComment);
            //更新 APP评论信息点赞
            Query query = Query.query(Criteria.where("authorId").is(user.getId()).and("commentId").is(apComment.getId()));
            ApCommentLike apCommentLike = mongoTemplate.findOne(query, ApCommentLike.class);
            apCommentLike.setOperation(dto.getOperation());
            mongoTemplate.save(apCommentLike);
        }

        //5.数据返回
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("likes",apComment.getLikes());
        return ResponseResult.okResult(resultMap);
    }

    @Override
    public ResponseResult findByArticleId(CommentDto dto) {
        //1.检查参数
        if(dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        if(dto.getSize() == null || dto.getSize() == 0){
            dto.setSize(20);
        }

        //2.按照文章id过滤，设置分页和排序
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("likes").lt(dto.getMinLikes()));
        query.limit(dto.getSize()).with(Sort.by(Sort.Direction.DESC,"likes"));
        List<ApComment> list = mongoTemplate.find(query, ApComment.class);

        //3.数据封装返回
        //3.1 用户未登录 加载数据
        ApUser user = AppThreadLocalUtils.getUser();
        if(user== null){
            return ResponseResult.okResult(list);
        }

        //3.2 用户已登录，加载数据，需要判断当前用户点赞了哪些评论
        List<String> idList = list.stream().map(x -> x.getId()).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentId").in(idList).and("authorId").is(user.getId()));
        List<ApCommentLike> apCommentLikes = mongoTemplate.find(query1, ApCommentLike.class);

        List<ApCommentVo> resultList = new ArrayList<>();

        if(apCommentLikes != null){

            list.stream().forEach(x->{
                ApCommentVo apCommentVo = new ApCommentVo();
                BeanUtils.copyProperties(x,apCommentVo);
                for (ApCommentLike apCommentLike : apCommentLikes) {
                    if(x.getId().equals(apCommentLike.getCommentId())){
                        apCommentVo.setOperation((short)0);
                        break;
                    }
                }
                resultList.add(apCommentVo);
            });
        }

        return ResponseResult.okResult(resultList);
    }
}
