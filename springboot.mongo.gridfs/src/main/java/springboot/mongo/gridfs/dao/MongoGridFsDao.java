/**
 * Copyright (c) 2017, www.wisdombud.com
 * All Rights Reserved.
 */
package springboot.mongo.gridfs.dao;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Repository;

import springboot.mongo.gridfs.entity.MongoUploadResult;

/**
 * 功能: Mongo GridFs Dao.<br/>
 * date: 2017年10月16日 下午6:05:58 <br/>
 *
 * @author zhaijunfeng
 * @version
 * @since JDK 1.7
 */
@Repository
public interface MongoGridFsDao {

    /**
     * 下载文件
     * 
     * @param fileId
     * @param response
     */
    public void downloadFile(String fileId, HttpServletResponse response);

    /**
     * 删除文件
     * 
     * @param fileId (删除多个以英文 , 隔开)
     */
    public Boolean deleteFile(String fileId);

    /**
     * 上传文件
     * 
     * @param request
     * @return
     */
    MongoUploadResult uploadFile(HttpServletRequest request);

    /**
     * 上传文件
     * 
     * @param request
     * @param map
     * @return
     */
    MongoUploadResult uploadFile(HttpServletRequest request, Map<String, String> map);

}
