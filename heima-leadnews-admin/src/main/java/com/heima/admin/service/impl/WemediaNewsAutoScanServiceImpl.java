package com.heima.admin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.heima.admin.feign.ArticleFeign;
import com.heima.admin.feign.WemediaFeign;
import com.heima.admin.mapper.AdChannelMapper;
import com.heima.admin.mapper.AdSensitiveMapper;
import com.heima.admin.service.WemediaNewsAutoScanService;
import com.heima.common.aliyun.GreeTextScan;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.fastdfs.FastDFSClient;
import com.heima.model.admin.dtos.NewsAuthDto;
import com.heima.model.admin.pojos.AdChannel;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.model.wemedia.vo.WmNewsVo;
import com.heima.utils.common.SensitiveWordUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@Log4j2
public class WemediaNewsAutoScanServiceImpl implements WemediaNewsAutoScanService {

    @Autowired
    private WemediaFeign wemediaFeign;

    @GlobalTransactional
    //@Override
    public void autoScanByMediaNewsId(Integer id) {
        if (id == null) {
            log.error("当前的审核id空");
            return;
        }
        //1.根据id查询自媒体文章信息
        WmNews wmNews = wemediaFeign.findById(id);
        if (wmNews == null) {
            log.error("审核的自媒体文章不存在，自媒体的id:{}", id);
            return;
        }

        //2.文章状态为4（人工审核通过）直接保存数据和创建索引
        if (wmNews.getStatus() == 4) {
            //保存数据
            saveAppArticle(wmNews);
            return;
        }

        //3.文章状态为8 发布时间小于等于当前时间 直接保存数据
        if (wmNews.getStatus() == 8 && wmNews.getPublishTime().getTime() <= System.currentTimeMillis()) {
            //保存数据
            saveAppArticle(wmNews);
            return;
        }

        //4.文章状态为1，待审核
        if (wmNews.getStatus() == 1) {
            //抽取文章内容中的纯文本和图片
            Map<String, Object> contentAndImagesResult = handleTextAndImages(wmNews);
            //4.1 文本审核
            /*boolean textScanBoolean = handleTextScan((String) contentAndImagesResult.get("content"), wmNews);
            if (!textScanBoolean) return;*/
            //4.2 图片审核
            /*boolean imagesScanBoolean = handleImagesScan((List<String>) contentAndImagesResult.get("images"), wmNews);
            if (!imagesScanBoolean) return;*/
            //4.3 自管理的敏感词审核
            boolean sensitiveScanBoolean = handleSensitive((String) contentAndImagesResult.get("content"), wmNews);
            if (!sensitiveScanBoolean) return;
            //4.4 发布时间大于当前时间，
            if (wmNews.getPublishTime().getTime() > System.currentTimeMillis()) {
                //修改文章状态为8
                updateWmNews(wmNews, (short) 8, "审核通过，待发布");
                return;
            }
            //5.审核通过，修改自媒体文章状态为9  保存app端相关文章信息
            saveAppArticle(wmNews);

        }


    }

    @Autowired
    private AdSensitiveMapper adSensitiveMapper;


    /**
     * 敏感词审核
     *
     * @param content
     * @param wmNews
     * @param wmNews
     * @return
     */

    private boolean handleSensitive(String content, WmNews wmNews) {

        boolean flag = true;

        List<String> allSensitive = adSensitiveMapper.findAllSensitive();
        //初始化敏感词
        SensitiveWordUtil.initMap(allSensitive);
        //文章内容自管理敏感词过滤
        Map<String, Integer> resultMap = SensitiveWordUtil.matchWords(content);
        if (resultMap.size() > 0) {
            log.error("敏感词过滤没有通过，包含了敏感词:{}", resultMap);
            //找到了敏感词，审核不通过
            updateWmNews(wmNews, (short) 2, "文章中包含了敏感词");
            flag = false;
        }

        return flag;
    }

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Value("${fdfs.url}")
    private String fileServerUrl;


    /**
     * 审核图片
     *
     * @param images
     * @param wmNews
     * @param wmNews
     * @return
     */

    private boolean handleImagesScan(List<String> images, WmNews wmNews) {
        if (images == null) {
            return true;
        }

        boolean flag = true;

        List<byte[]> imageList = new ArrayList<>();

        try {
            for (String image : images) {
                String imageName = image.replace(fileServerUrl, "");
                int index = imageName.indexOf("/");
                String groupName = imageName.substring(0, index);
                String imagePath = imageName.substring(index + 1);
                byte[] imageByte = fastDFSClient.download(groupName, imagePath);
                imageList.add(imageByte);
            }
            //阿里云图片审核
            Map map = greenImageScan.imageScan(imageList);
            //审核不通过
            if (!map.get("suggestion").equals("pass")) {
                //审核失败
                if (map.get("suggestion").equals("block")) {
                    //修改自媒体文章的状态，并告知审核失败原因
                    updateWmNews(wmNews, (short) 2, "文章中图片有违规");
                    flag = false;
                }

                //人工审核
                if (map.get("suggestion").equals("review")) {
                    //修改自媒体文章的状态，并告知审核失败原因
                    updateWmNews(wmNews, (short) 3, "文章图片有不确定元素");
                    flag = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;

    }


    @Autowired
    private GreeTextScan greeTextScan;


    /**
     * 文本审核
     *
     * @param content
     * @param wmNews
     * @return
     */

    private boolean handleTextScan(String content, WmNews wmNews) {
        boolean flag = true;
        try {
            Map map = greeTextScan.greeTextScan(content);
            //审核不通过
            if (!map.get("suggestion").equals("pass")) {
                //审核失败
                if (map.get("suggestion").equals("block")) {
                    //修改自媒体文章的状态，并告知审核失败原因
                    updateWmNews(wmNews, (short) 2, "文章内容中有敏感词汇");
                    flag = false;
                }

                //人工审核
                if (map.get("suggestion").equals("review")) {
                    //修改自媒体文章的状态，并告知审核失败原因
                    updateWmNews(wmNews, (short) 3, "文章内容中有不确定词汇");
                    flag = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }

        return flag;
    }


    /**
     * 修改自媒体文章
     *
     * @param wmNews
     * @param status
     * @param msg
     */

    private void updateWmNews(WmNews wmNews, short status, String msg) {
        wmNews.setStatus(status);
        wmNews.setReason(msg);
        wemediaFeign.updateWmNews(wmNews);
    }


    /**
     * 提取文本内容和图片
     *
     * @param wmNews
     * @return
     */

    private Map<String, Object> handleTextAndImages(WmNews wmNews) {

        //文章的内容
        String content = wmNews.getContent();

        //存储纯文本内容
        StringBuilder sb = new StringBuilder();
        //存储图片
        List<String> images = new ArrayList<>();

        List<Map> contentList = JSONArray.parseArray(content, Map.class);
        for (Map map : contentList) {
            if (map.get("type").equals("text")) {
                sb.append(map.get("value"));
            }

            if (map.get("type").equals("image")) {
                images.add((String) map.get("value"));
            }
        }

        if (wmNews.getImages() != null && wmNews.getType() != 0) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", sb.toString());
        resultMap.put("images", images);
        return resultMap;

    }

    @Autowired
    ArticleFeign articleFeign;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    /**
     * 保存app文章相关的数据
     *
     * @param wmNews
     */

    private void saveAppArticle(WmNews wmNews) {
        //保存app文章
        ApArticle apArticle = saveArticle(wmNews);
        //保存app文章配置
        saveArticleConfig(apArticle);
        //保存app文章内容
        saveArticleContent(apArticle, wmNews);

        //修改自媒体文章的状态为9
        wmNews.setArticleId(apArticle.getId());
        updateWmNews(wmNews, (short) 9, "审核通过");

        //es索引创建
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("id",apArticle.getId());
        map.put("publishTime",apArticle.getPublishTime());
        map.put("layout",apArticle.getLayout());
        map.put("images",apArticle.getImages());
        map.put("authorId",apArticle.getAuthorId());
        map.put("title",apArticle.getTitle());
        //保存到es索引库
        IndexRequest indexRequest = new IndexRequest("app_info_article").id(apArticle.getId().toString()).source(map);
        try {
            restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * 创建app端文章内容信息
     *
     * @param apArticle
     * @param wmNews
     */

    private void saveArticleContent(ApArticle apArticle, WmNews wmNews) {
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(wmNews.getContent());
        articleFeign.saveArticleContent(apArticleContent);
    }


    /**
     * 创建app端文章配置信息
     *
     * @param apArticle
     */

    private void saveArticleConfig(ApArticle apArticle) {
        ApArticleConfig apArticleConfig = new ApArticleConfig();
        apArticleConfig.setArticleId(apArticle.getId());
        apArticleConfig.setIsForward(true);
        apArticleConfig.setIsDelete(false);
        apArticleConfig.setIsDown(true);
        apArticleConfig.setIsComment(true);

        articleFeign.saveArticleConfig(apArticleConfig);
    }

    @Autowired
    AdChannelMapper adChannelMapper;


    /**
     * 保存文章
     *
     * @param wmNews
     * @return
     */

    private ApArticle saveArticle(WmNews wmNews) {
        ApArticle apArticle = new ApArticle();
        apArticle.setTitle(wmNews.getTitle());
        apArticle.setLayout(wmNews.getType());
        apArticle.setImages(wmNews.getImages());
        apArticle.setCreatedTime(new Date());
        apArticle.setPublishTime(new Date());

        //获取作者相关信息
        Integer wmUserId = wmNews.getUserId();
        WmUser wmUser = wemediaFeign.findWmUserById(wmUserId);
        if (wmUser != null) {
            String wmUserName = wmUser.getName();
            ApAuthor apAuthor = articleFeign.selectAuthorByName(wmUserName);
            if (apAuthor != null) {
                apArticle.setAuthorId(apAuthor.getId().longValue());
                apArticle.setAuthorName(apAuthor.getName());
            }

        }

        //获取频道相关信息
        Integer channelId = wmNews.getChannelId();
        AdChannel channel = adChannelMapper.selectById(channelId);
        if (channel != null) {
            apArticle.setChannelId(channel.getId());
            apArticle.setChannelName(channel.getName());
        }

        return articleFeign.saveAparticle(apArticle);
    }

    @Override
    public PageResponseResult findNews(NewsAuthDto dto) {
        //分页查询
        PageResponseResult responseResult = wemediaFeign.findList(dto);
        //返回的数据中有图片需要显示，需要回显一个fasfdfs服务器的地址
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    @Override
    public ResponseResult findOne(Integer id) {
        //1.参数检查
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询数据
        WmNewsVo wmNewsVo = wemediaFeign.findWmNewsVo(id);

        //3.结果封装
        ResponseResult responseResult = ResponseResult.okResult(wmNewsVo);
        responseResult.setHost(fileServerUrl);
        return responseResult;
    }

    @Override
    public ResponseResult updateStatus(NewsAuthDto dto, Integer type) {
        //1.参数检查
        if(dto == null || dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询文章信息
        WmNews wmNews = wemediaFeign.findById(dto.getId());
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //3.审核失败
        if(type.equals(0)){
            updateWmNews(wmNews,(short)2,dto.getMsg());
        }else if (type.equals(1)){
            //4.审核成功
            updateWmNews(wmNews,(short)4,"人工审核通过");
        }


        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}

