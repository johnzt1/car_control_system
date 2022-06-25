package com.heima.search.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.heima.model.behavior.pojos.ApBehaviorEntry;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.feign.BehaviorFeign;
import com.heima.search.service.ApUserSearchService;
import com.heima.search.service.ArticleSearchService;
import com.heima.utils.threadlocal.AppThreadLocalUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
@Log4j2
public class ArticleSearchServiceImpl implements ArticleSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ApUserSearchService apUserSearchService;
//    @Autowired
//    ElasticsearchTemplate elasticsearchTemplate;
    @Override
    public ResponseResult search(UserSearchDto dto) throws IOException {
        //1.检查参数
        if(dto == null || StringUtils.isBlank(dto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //只有在首页查询的时候才会保存
        if(dto.getFromIndex() == 0){
            ApBehaviorEntry apBehaviorEntry = getEntry(dto);
            if(apBehaviorEntry == null){
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
            }
            apUserSearchService.insert(apBehaviorEntry.getId(),dto.getSearchWords());
        }

        //2.从es中检索数据
        SearchRequest searchRequest = new SearchRequest("app_info_article");
        //创建条件构建器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //创建布尔查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //根据关键字模糊查询
        boolQueryBuilder.must(QueryBuilders.wildcardQuery("title",dto.getSearchWords()));
        //查询小于当前时间的数据
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("publishTime").lte(dto.getMinBehotTime()));

        searchSourceBuilder.query(boolQueryBuilder);
        //根据发布时间倒序排序
        searchSourceBuilder.sort("publishTime", SortOrder.DESC);
        //分页查询
        searchSourceBuilder.from(dto.getPageNum());
        searchSourceBuilder.size(dto.getPageSize());
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //3.从hits获取数据，封装返回
        List<Map> articleList = new ArrayList<>();

        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Map map = JSONObject.parseObject(sourceAsString, Map.class);
            articleList.add(map);
        }
        return ResponseResult.okResult(articleList);
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

}
