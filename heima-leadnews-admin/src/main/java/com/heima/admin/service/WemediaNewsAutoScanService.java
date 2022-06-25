package com.heima.admin.service;

import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;

public interface WemediaNewsAutoScanService {

    /**
     * 自媒体文章审核
     * @param id
     */
    public void autoScanByMediaNewsId(Integer id);

    /**
     * 查询自媒体文章列表
     * @param dto
     * @return
     */
    public PageResponseResult findNews(NewsAuthDto dto);

    /**
     * 查询详情
     * @param id
     * @return
     */
    public ResponseResult findOne(Integer id);

    /**
     * 审核通过或驳回
     * @param dto
     * @param type 0 为驳回  1为通过
     * @return
     */
    public ResponseResult updateStatus(NewsAuthDto dto,Integer type);

}
