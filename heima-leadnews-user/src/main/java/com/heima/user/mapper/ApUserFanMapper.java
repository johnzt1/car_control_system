package com.heima.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.user.pojos.ApUserFan;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * APP用户粉丝信息表 Mapper 接口
 * </p>
 *
 * @author itheima
 */
@Mapper
public interface ApUserFanMapper extends BaseMapper<ApUserFan> {
}