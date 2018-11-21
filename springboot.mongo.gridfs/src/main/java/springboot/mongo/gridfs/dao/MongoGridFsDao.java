
package springboot.mongo.gridfs.dao;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Repository;

import springboot.mongo.gridfs.entity.MongoUploadResult;

/**
 * 功能: Mongo GridFs Dao.原生写法，建议使用 GridFsTemplate<br/>
 * date: 2017年10月16日 下午6:05:58 <br/>
 *
 * @author zhaijunfeng
 * @version
 * @since JDK 1.7
 */
@Deprecated
@Repository
public interface MongoGridFsDao {

	/**
	 * 下载文件
	 * 
	 * @param fileId
	 * @param response
	 */
	@Deprecated
	public void downloadFile(String fileId, HttpServletResponse response);

	/**
	 * 删除文件
	 * 
	 * @param fileId
	 *            (删除多个以英文 , 隔开)
	 */
	@Deprecated
	public Boolean deleteFile(String fileId);

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @return
	 */
	@Deprecated
	MongoUploadResult uploadFile(HttpServletRequest request);

	/**
	 * 上传文件
	 * 
	 * @param request
	 * @param map
	 * @return
	 */
	@Deprecated
	MongoUploadResult uploadFile(HttpServletRequest request, Map<String, String> map);

}
