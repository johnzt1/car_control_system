package com.heima.apis.wemedia;

import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vo.WmNewsVo;

import java.util.List;

public interface WmNewsControllerApi {

    /**
     * 分页带条件查询自媒体文章列表
     * @param dto
     * @return
     */
    public ResponseResult findAll(WmNewsPageReqDto dto);

    /**
     * 保存、修改文章，保存草稿
     * @return
     */
    public ResponseResult submitNews(WmNewsDto dto);

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
     * 根据id查询文章信息
     * @param id
     * @return
     */
    public WmNews findById(Integer id);

    /**
     * 修改文章
     * @param wmNews
     * @return
     */
    public ResponseResult updateWmNews(WmNews wmNews);

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
