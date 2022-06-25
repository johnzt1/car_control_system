package com.heima.admin.job;

import com.hankcs.hanlp.dependency.nnparser.util.Log;
import com.heima.admin.feign.WemediaFeign;
import com.heima.admin.service.WemediaNewsAutoScanService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
public class WeMediaNewsAutoScanJob {

    @Autowired
    private WemediaFeign wemediaFeign;

    @Autowired
    private WemediaNewsAutoScanService wemediaNewsAutoScanService;

    @XxlJob("wemediaAutoScanJob")
    public ReturnT newsAutoScanJob(String param){
        log.info("自媒体文章审核调度任何开始....");
        //1.查询符合条件的文章信息
        List<Integer> idList = wemediaFeign.findRelease();
        if(idList != null && !idList.isEmpty()){
            for (Integer id : idList) {
                wemediaNewsAutoScanService.autoScanByMediaNewsId(id);
            }
        }

        //2.调用自动审核
        log.info("自媒体文章审核调度任何结束....");
        return ReturnT.SUCCESS;
    }
}
