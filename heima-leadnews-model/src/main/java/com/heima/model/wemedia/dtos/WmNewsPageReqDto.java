package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
public class WmNewsPageReqDto extends PageRequestDto {

    /**
     * 状态
     */
    private Short status;
    /**
     * 关键字
     */
    private String keyword;

    /**
     * 频道
     */
    private Integer channelId;

    /**
     * 开始时间
     */
    private Date beginPubDate;

    /**
     * 结束时间
     */
    private Date endPubDate;
}
