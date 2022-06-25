package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 素材-上传图片
     * @param multipartFile
     * @return
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 查询素材列表
     * @return
     */
    public ResponseResult findList(WmMaterialDto dto);

    /**
     * 根据id删除图片
     * @param id
     * @return
     */
    public ResponseResult delPicture(Integer id);

    /**
     * 收藏与取消收藏
     * @param id
     * @param type
     * @return
     */
    public ResponseResult updateStatus(Integer id,Short type);

}
