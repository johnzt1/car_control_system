package com.heima.article.controller.v1;

import com.heima.apis.article.ArticleHomeControllerApi;
import com.heima.article.service.ApArticleService;
import com.heima.common.constans.article.ArticleConstans;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController implements ArticleHomeControllerApi {

    @Autowired
    private ApArticleService articleService;

    @PostMapping("/load")
    @Override
    public ResponseResult load(@RequestBody ArticleHomeDto dto) {
        return articleService.load(dto, ArticleConstans.LOADTYPE_LOAD_MORE);
    }

    @PostMapping("/loadmore")
    @Override
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto) {
        return articleService.load(dto, ArticleConstans.LOADTYPE_LOAD_MORE);
    }

    @PostMapping("/loadnew")
    @Override
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto) {
        return articleService.load(dto,ArticleConstans.LOADTYPE_LOAD_NEW);
    }
}
