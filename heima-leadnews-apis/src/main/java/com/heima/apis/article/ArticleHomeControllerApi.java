package com.heima.apis.article;

import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ArticleHomeControllerApi {


    /**
     * 加载首页文章
     * @return
     */
    public ResponseResult load(ArticleHomeDto dto);

    /**
     * 加载更多
     * @return
     */
    public ResponseResult loadMore(ArticleHomeDto dto);

    /**
     * 加载最新
     * @return
     */
    public ResponseResult loadNew(ArticleHomeDto dto);
}
