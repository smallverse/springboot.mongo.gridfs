package springboot.mongo.gridfs;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import springboot.mongo.gridfs.dao.GridFsTemplateDao;

/**
 * 建议使用 spring GridFsTemplate oper 可用于权限验证（须使用当前数据库下的用户、root用户如果不分配也访问不了）
 * 
 * @author zhaijunfeng
 */
@RestController
public class GridfsTemplateController {
	private static final Logger LOGGER = LoggerFactory.getLogger(GridfsTemplateController.class);

	@Autowired
	private GridFsTemplateDao gridFsTemplateDao;

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/mongo-temp/upload", method = RequestMethod.POST)
	public @ResponseBody Object handleFileUpload(HttpServletRequest request) throws IOException {
		return gridFsTemplateDao.uploadFile(request);
	}

	/**
	 * @param request
	 * @param map
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/mongo-temp/upload-with-metadata", method = RequestMethod.POST)
	public @ResponseBody Object handleFileUpload(HttpServletRequest request, Map<String, Object> map)
			throws IOException {
		return gridFsTemplateDao.uploadFile(request, map);
	}

	/**
	 * @param id
	 * @param response
	 */
	@RequestMapping(value = "/mongo-temp/download", method = RequestMethod.GET)
	public void getFile(String id, HttpServletResponse response) {
		LOGGER.info("getFile id:" + id);
		gridFsTemplateDao.downloadFile(id, response);
	}

	/**
	 * @param id
	 * @param response
	 */
	@RequestMapping(value = "/mongo-temp/delete", method = RequestMethod.POST)
	public Boolean deleteFile(String id, HttpServletResponse response) {
		LOGGER.info("deleteFile id:" + id);
		return gridFsTemplateDao.deleteFile(id);
	}
}
