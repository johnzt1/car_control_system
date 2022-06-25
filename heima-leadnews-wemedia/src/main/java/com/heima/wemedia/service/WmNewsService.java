package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vo.WmNewsVo;

import java.util.List;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 分页带条件查询自媒体文章列表
     * @param dto
     * @return
     */
    public ResponseResult findAll(WmNewsPageReqDto dto);

    /**
     * 自媒体文章发布
     * @param dto
     * @param isSubmit 是否为提交  1 提交  0 草稿
     * @return
     */
    public ResponseResult saveNews(WmNewsDto dto,Short isSubmit);

    /**
     * 根据id获取文章信息
     * @param id
     * @return
     */
    public ResponseResult findWmNewsById(Integer id);

    /**
     * 删除文章
     * @param id
     * @return
     */
    public ResponseResult delNews(Integer id);

    /**
     * 文章上下架
     * @param dto
     * @return
     */
    public ResponseResult downOrUp(WmNewsDto dto);

    /**
     * 查询待发布的文章
     * @return
     */
    public List<Integer> findRelease();

    /**
     * 根据标题模糊分页查询文章信息
     * @param dto
     * @return
     */
    public PageResponseResult findList(NewsAuthDto dto);

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    public WmNewsVo findWmNewsVo(Integer id);

}
