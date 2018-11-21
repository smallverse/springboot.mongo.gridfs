package springboot.mongo.gridfs.dao;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Repository;

import springboot.mongo.gridfs.entity.MongoUploadResult;

@Repository
public interface GridFsTemplateDao {
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
    MongoUploadResult uploadFile(HttpServletRequest request, Map<String, Object> map);
}
