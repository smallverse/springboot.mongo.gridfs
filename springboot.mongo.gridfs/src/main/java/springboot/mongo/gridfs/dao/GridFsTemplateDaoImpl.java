package springboot.mongo.gridfs.dao;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.google.common.collect.Lists;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

import cn.hutool.core.io.FileUtil;
import springboot.mongo.gridfs.EnumFileType;
import springboot.mongo.gridfs.conf.GlobalVariableConf;
import springboot.mongo.gridfs.entity.MongoFileInfo;
import springboot.mongo.gridfs.entity.MongoUploadResult;

/**
 * 若无须auth，则注释： #spring.data.mongodb.authentication-database=
 * #spring.data.mongodb.username= #spring.data.mongodb.password=
 * 
 * @author zhaijunfeng
 */
@Repository
public class GridFsTemplateDaoImpl implements GridFsTemplateDao {

	private static Logger LOGGER = LoggerFactory.getLogger(MongoGridFsDaoImpl.class);

	private static final String F_ID = "f_id";
	private static final String FILE_NAME = "filename";
	private static final String CN_COMMA = "，";
	private static final String EN_COMMA = ",";

	@Autowired
	private GridFsTemplate gridFsTemplate;

	@Autowired
	private GlobalVariableConf globalVariableConf;

	@Override
	public MongoUploadResult uploadFile(HttpServletRequest request) {
		return uploadFile(request, new HashMap<String, Object>());
	}

	@Override
	public MongoUploadResult uploadFile(HttpServletRequest request, Map<String, Object> map) {
		MongoUploadResult result = new MongoUploadResult();
		List<MongoFileInfo> mongoFiles = Lists.newArrayList();
		boolean isSuccess = false;
		String msg = "";
		MongoFileInfo mongoFileInfo = null;

		try {

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

			GridFSFile gridFSFile = null;
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

					map.put(FILE_NAME, fileName);
					String uuid = UUID.randomUUID().toString().replaceAll("-", "");
					map.put(F_ID, uuid);

					gridFSFile = gridFsTemplate.store(in, fileName, partFile.getContentType());
					if (null != gridFSFile) {
						for (String key : map.keySet()) {
							gridFSFile.put(key, map.get(key));
						}
						gridFSFile.save();
					}

					// fileIds
					mongoFileInfo = new MongoFileInfo(uuid, fileName);
					mongoFiles.add(mongoFileInfo);
					// local cache
					if (null != this.globalVariableConf && this.globalVariableConf.getIsCacheFile()
							&& StringUtils.isNotBlank(this.globalVariableConf.getCacheFileType())) {

						mongoFileInfo.setServerPath(
								cacheLocalFile(this.globalVariableConf.getCacheFileType(), fileName, uuid));
					}
				}
			}

			if (0 < mongoFiles.size()) {
				isSuccess = true;
				msg = "上传成功";
			}

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			msg = "上传失败";
		} catch (MaxUploadSizeExceededException e) {
			msg = "文件应不大于" + e.getMaxUploadSize();
			LOGGER.error(msg, e);
		} catch (Exception e) {
			msg = e.getMessage();
			LOGGER.error(msg, e);
		}

		result.setFiles(mongoFiles);
		result.setMsg(msg);
		result.setSuccess(isSuccess);

		return result;
	}

	private String cacheLocalFile(String cacheFileType, String fileName, String uuid) {
		String relativePath = "";
		String extension = "";
		int i = fileName.toLowerCase().lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
		String uploadDir = "";
		if (cacheFileType.equals(EnumFileType.PIC.getValue())) {
			LOGGER.debug("file extension:" + extension);
			if (!isPic(extension)) {
				LOGGER.debug("extension is not pic:" + extension);
				return relativePath;
			}
			uploadDir = EnumFileType.PIC.getValue().toLowerCase();

		} else if (cacheFileType.equals(EnumFileType.ALL.getValue())) {
			uploadDir = EnumFileType.ALL.getValue().toLowerCase();
		} else {
			LOGGER.debug("暂不处理 cacheFileType:" + cacheFileType);
			return relativePath;
		}

		File upload = null;
		try {
			upload = new File(ResourceUtils.getURL("classpath:").getPath(), "static/upload/" + uploadDir + "/");
			if (!upload.exists()) {
				upload.mkdirs();
			}
			LOGGER.debug("upload url:" + upload.getAbsolutePath());
			String fullFilePath = upload.getAbsolutePath() + "//" + uuid + fileName;

			Query query = new Query();
			query.addCriteria(Criteria.where(F_ID).is(uuid));
			GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(query);
			FileUtil.writeFromStream(gridFSDBFile.getInputStream(), fullFilePath);
			relativePath = "/upload/" + uploadDir + "/" + uuid + fileName;
		} catch (Exception e) {
			relativePath = "";
			LOGGER.error(e.getMessage(), e);
		}

		return relativePath;

	}

	@Override
	public void downloadFile(String fileId, HttpServletResponse response) {
		try {
			if (StringUtils.isBlank(fileId)) {
				LOGGER.error("downloadFile, fileId is blank");
				return;
			}

			if (fileId.contains(CN_COMMA)) {
				fileId.replace(CN_COMMA, EN_COMMA);
			}

			Query query = new Query();
			if (fileId.contains(EN_COMMA)) {
				String[] ids = fileId.split(EN_COMMA);
				query.addCriteria(Criteria.where(F_ID).in(ids));
			} else {
				query.addCriteria(Criteria.where(F_ID).is(fileId));
			}

			List<GridFSDBFile> results = gridFsTemplate.find(query);
			if (null == results) {
				return;
			}
			for (GridFSDBFile result : results) {
				writeSingleFile(response, result);
			}
		} catch (Exception e) {
			LOGGER.error("downloadFile:" + e.getMessage(), e);
		}
	}

	private void writeSingleFile(HttpServletResponse response, GridFSDBFile result)
			throws UnsupportedEncodingException, IOException {
		response.reset();
		response.setContentType("application/octet-stream");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setHeader("Content-Disposition",
				"inline; filename=" + new String((result.getFilename()).getBytes("gb2312"), "ISO8859-1"));
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			// GridFSDBFile向客户端输出文件,
			// 避免使用os.write输出固定长度（如1024）的字节数组而引起文件下载后打开异常的问题
			result.writeTo(os);
			os.flush();
		} catch (Exception e) {
			LOGGER.error("writeSingleFile:" + e.getMessage(), e);
		} finally {
			os.close();
		}

	}

	@Override
	public Boolean deleteFile(String fId) {
		boolean isSuccess = false;
		try {
			Query query = new Query();
			if (StringUtils.isBlank(fId)) {
				return false;
			}

			if (fId.contains(CN_COMMA)) {
				fId.replace(CN_COMMA, EN_COMMA);
			}

			if (fId.contains(EN_COMMA)) {
				String[] ids = fId.split(EN_COMMA);
				query.addCriteria(Criteria.where(F_ID).in(ids));
			} else {
				query.addCriteria(Criteria.where(F_ID).is(fId));
			}

			gridFsTemplate.delete(query);

			isSuccess = true;
		} catch (Exception e) {

			LOGGER.error("deleteFile:" + e.getMessage(), e);

		}

		return isSuccess;

	}

	private boolean isPic(String type) {
		List<String> pics = Lists.newArrayList("png", "jpg", "jpeg", "gif", "bmp");
		return pics.contains(type);
	}

}
