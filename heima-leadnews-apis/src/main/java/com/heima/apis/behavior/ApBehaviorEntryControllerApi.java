package com.heima.apis.behavior;

import com.heima.model.behavior.pojos.ApBehaviorEntry;

public interface ApBehaviorEntryControllerApi {

    /**
     * 查询行为实体
     * @param userId
     * @param equipmentId
     * @return
     */
    public ApBehaviorEntry findByUserIdOrEquipmentId(Integer userId, Integer equipmentId);
}
