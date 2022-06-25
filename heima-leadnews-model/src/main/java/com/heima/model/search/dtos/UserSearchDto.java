package com.heima.model.search.dtos;

import com.heima.model.common.annotation.IdEncrypt;
import lombok.Data;

import java.util.Date;

@Data
public class UserSearchDto {

    // 设备ID
    @IdEncrypt
    Integer equipmentId;
    /**
    * 搜索关键字
    */
    String searchWords;
    /**
    * 当前页
    */
    int pageNum;
    /**
    * 分页条数
    */
    int pageSize;
    /**
    * 最小时间
    */
    Date minBehotTime;

    /**
     * 接收搜索历史记录id
     */
    Integer id;

    public int getFromIndex(){
        if(this.pageNum<1)return 0;
        if(this.pageSize<1) this.pageSize = 10;
        return this.pageSize * (pageNum-1);
    }
}