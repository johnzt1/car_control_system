package com.heima.apis.article;

import com.heima.model.article.pojos.ApAuthor;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.web.bind.annotation.PathVariable;

public interface AuthorControllerApi  {

    /**
     * 根据用户id查询作者
     * @param id
     * @return
     */
    public ApAuthor findByUserId(Integer id);

    /**
     * 保存作者
     * @param apAuthor
     * @return
     */
    public ResponseResult save(ApAuthor apAuthor);

    /**
     * 根据名称查询作者
     * @param name
     * @return
     */
    public ApAuthor selectAuthorByName(String name);

    /**
     * 根据id查询作者
     * @param id
     * @return
     */
    public ApAuthor findById(Integer id);

}
