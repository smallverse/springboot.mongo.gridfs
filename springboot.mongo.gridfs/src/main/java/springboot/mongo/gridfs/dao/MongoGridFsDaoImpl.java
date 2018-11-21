package springboot.mongo.gridfs.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import springboot.mongo.gridfs.conf.MongoGridFsConf;
import springboot.mongo.gridfs.entity.MongoFileInfo;
import springboot.mongo.gridfs.entity.MongoUploadResult;

/**
 * com.mongodb.gridfs spring.data.mongodb.username 决定auth，若无须auth，则注释：
 * #spring.data.mongodb.authentication-database= #spring.data.mongodb.username=
 * #spring.data.mongodb.password= 原生写法，建议使用 GridFsTemplate
 * 
 * @author zhaijunfeng
 */
@Deprecated
@SuppressWarnings("deprecation")
@Repository
public class MongoGridFsDaoImpl implements MongoGridFsDao {
	private static Logger LOGGER = LoggerFactory.getLogger(MongoGridFsDaoImpl.class);

	private static final String F_ID = "f_id";
	private static final String FILE_NAME = "filename";
	private static final String CONTENT_TYPE = "contentType";
	private static final String CN_COMMA = "，";
	private static final String EN_COMMA = ",";

	@Autowired
	private MongoGridFsConf mongoGridFsConf;

	private Mongo mongo;

	private GridFS gridFS;

	@Override
	public MongoUploadResult uploadFile(HttpServletRequest request) {
		return uploadFile(request, null);
	}

	@Override
	public MongoUploadResult uploadFile(HttpServletRequest request, Map<String, String> map) {
		MongoUploadResult result = new MongoUploadResult();
		List<MongoFileInfo> mongoFiles = Lists.newArrayList();
		boolean isSuccess = false;
		String msg = "";

		GridFSInputFile file = null;
		try {
			this.gridFS = getGridFs();
			CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
					request.getSession().getServletContext());

			if (!multipartResolver.isMultipart(request)) {
				result.setSuccess(isSuccess);
				result.setFiles(mongoFiles);
				result.setMsg("is not Multipart");
				return result;
			}

			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			MultiValueMap<String, MultipartFile> multipartFiles = multiRequest.getMultiFileMap();

			for (Entry<String, List<MultipartFile>> partFiles : multipartFiles.entrySet()) {
				if (null == partFiles) {
					continue;
				}

				for (MultipartFile partFile : partFiles.getValue()) {
					if (null == partFile) {
						continue;
					}
					String fileName = partFile.getOriginalFilename();
					InputStream in = partFile.getInputStream();
					file = gridFS.createFile(in);
					String uuid = setFileAttr(map, partFile, file, fileName);
					file.save();

					// fileIds
					mongoFiles.add(new MongoFileInfo(uuid, fileName));
				}
			}

			if (0 < mongoFiles.size()) {
				isSuccess = true;
				msg = "上传成功";
			}

		} catch (IOException e) {
			LOGGER.error("IOException uploadFile:" + e.getMessage(), e);
			msg = "上传失败";
		} catch (MaxUploadSizeExceededException e) {
			msg = "文件应不大于" + e.getMaxUploadSize();
			LOGGER.error(msg, e);
		} catch (Exception e) {
			msg = e.getMessage();
			LOGGER.error(msg, e);
		} finally {
			try {
				mongo.close();
				mongo = null;// 一定要写这句话，不然系统不会回收，只是关闭了，连接存在
			} catch (Exception e) {
				LOGGER.error("mongo.close:" + e.getMessage(), e);
			}

		}

		result.setFiles(mongoFiles);
		result.setMsg(msg);
		result.setSuccess(isSuccess);

		return result;
	}

	private String setFileAttr(Map<String, String> map, MultipartFile partFile, GridFSInputFile file, String fileName) {
		file.put(FILE_NAME, fileName);
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		file.put(F_ID, uuid);
		file.put(CONTENT_TYPE, partFile.getContentType());
		if (null != map) {
			for (String key : map.keySet()) {
				file.put(key, map.get(key));
			}
		}
		return uuid;
	}

	@Override
	public void downloadFile(String fileId, HttpServletResponse response) {
		OutputStream os = null;
		try {
			this.gridFS = getGridFs();
			DBObject query = new BasicDBObject(F_ID, fileId);
			GridFSDBFile gridFSFile = gridFS.findOne(query);
			if (null == gridFSFile) {
				return;
			}

			response.reset();
			response.setContentType("application/octet-stream");
			response.setHeader("Pragma", "No-cache");
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setHeader("Content-Disposition",
					"inline; filename=" + new String((gridFSFile.getFilename()).getBytes("gb2312"), "ISO8859-1"));

			os = response.getOutputStream();
			// GridFSDBFile向客户端输出文件,
			// 避免使用os.write输出固定长度（如1024）的字节数组而引起文件下载后打开异常的问题
			gridFSFile.writeTo(os);
			os.flush();

		} catch (Exception e) {
			LOGGER.error("downloadFile:" + e.getMessage(), e);
		} finally {
			try {
				os.close();
				mongo.close();
				mongo = null;// 一定要写这句话，不然系统不会回收，只是关闭了，连接存在
			} catch (Exception e) {
				LOGGER.error("mongo.close:" + e.getMessage(), e);
			}
		}
	}

	@Override
	public Boolean deleteFile(String fId) {
		boolean isSuccess = false;
		try {
			this.gridFS = getGridFs();
			BasicDBObject query = new BasicDBObject();
			if (StringUtils.isBlank(fId)) {
				return false;
			}

			if (fId.contains(CN_COMMA)) {
				fId.replace(CN_COMMA, EN_COMMA);
			}

			if (fId.contains(EN_COMMA)) {
				String[] ids = fId.split(EN_COMMA);
				for (String id : ids) {
					query.append(F_ID, id);
				}
			} else {
				query.append(F_ID, fId);
			}
			// query中无条件，删除所有
			gridFS.remove(query);

			isSuccess = true;
		} catch (Exception e) {
			LOGGER.error("deleteFile:" + e.getMessage(), e);

		}

		return isSuccess;

	}

	public GridFS getGridFs() {
		if (StringUtils.isEmpty(mongoGridFsConf.getUsername())) {
			return getNoAuthGridFs();
		} else {
			return getAuthGridFs();
		}

	}

	public GridFS getAuthGridFs() {

		try {
			mongo = new Mongo(mongoGridFsConf.getHost(), mongoGridFsConf.getPort());
			DB mongoDb = mongo.getDB(mongoGridFsConf.getDatabase());
			// 认证权限
			boolean authFlag = mongoDb.authenticate(mongoGridFsConf.getUsername(),
					mongoGridFsConf.getPassword().toCharArray());
			LOGGER.error("authFlag :" + authFlag);
			return authFlag ? new GridFS(mongoDb) : null;
		} catch (UnknownHostException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}

	}

	public GridFS getNoAuthGridFs() {
		try {
			mongo = new Mongo(mongoGridFsConf.getHost(), mongoGridFsConf.getPort());
			DB mongoDb = mongo.getDB(mongoGridFsConf.getDatabase());
			return new GridFS(mongoDb);
		} catch (UnknownHostException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}

	}

}
