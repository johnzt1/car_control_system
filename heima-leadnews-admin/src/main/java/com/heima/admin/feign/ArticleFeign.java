package com.heima.admin.feign;

import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient("leadnews-article")
public interface ArticleFeign {

    @PostMapping("/api/v1/article/save")
    public ApArticle saveAparticle(ApArticle apArticle);

    @PostMapping("/api/v1/article_config/save")
    public ResponseResult saveArticleConfig(ApArticleConfig apArticleConfig);

    @PostMapping("/api/v1/article_content/save")
    public ResponseResult saveArticleContent(ApArticleContent apArticleContent);

    @GetMapping("/api/v1/author/findByName/{name}")
    public ApAuthor selectAuthorByName(@PathVariable("name") String name);



}
