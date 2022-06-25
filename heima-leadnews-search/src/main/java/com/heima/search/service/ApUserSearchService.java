package com.heima.search.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;
import com.heima.model.search.pojos.ApUserSearch;

public interface ApUserSearchService extends IService<ApUserSearch> {

    /**
     * 查询搜索历史列表
     * @param userSearchDto
     * @return
     */
    public ResponseResult findUserSearch(UserSearchDto userSearchDto);

    /**
     * 删除历史记录
     * @param userSearchDto
     * @return
     */
    public ResponseResult delUserSearch(UserSearchDto userSearchDto);

    /**
     * 插入搜索记录
     * @param entryId
     * @param searchWords
     */
    public void insert(Integer entryId,String searchWords);
}
