package com.heima.apis.search;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;
import org.apache.hadoop.hbase.protobuf.generated.AccessControlProtos;

public interface ApUserSearchControllerApi {


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
}
