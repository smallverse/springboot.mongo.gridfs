package springboot.mongo.gridfs;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import springboot.mongo.gridfs.dao.MongoGridFsDao;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 原生写法，建议使用 GridFsTemplate 可开启权限验证（须使用当前数据库下的用户、root用户如果不分配也访问不了） mongo GridFS
 * 
 * @author zhaijunfeng
 */
@Deprecated
@ApiIgnore
@RestController
public class MongoGridfsController {
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoGridfsController.class);

	@Autowired
	protected MongoGridFsDao gridFsDao;

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/mongo-grid/upload", method = RequestMethod.POST)
	public @ResponseBody Object handleFileUpload(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		return gridFsDao.uploadFile(request);
	}

	/**
	 * @param id
	 * @param response
	 */
	@RequestMapping(value = "/mongo-grid/download", method = RequestMethod.GET)
	public void getFile(String id, HttpServletResponse response) {
		LOGGER.info("getFile id:" + id);
		gridFsDao.downloadFile(id, response);
	}

	/**
	 * @param id
	 * @param response
	 */
	@RequestMapping(value = "/mongo-grid/delete", method = RequestMethod.POST)
	public Boolean deleteFile(String id, HttpServletResponse response) {
		LOGGER.info("deleteFile id:" + id);
		return gridFsDao.deleteFile(id);
	}
}
