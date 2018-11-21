package springboot.mongo.gridfs.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 自动以全局配置
 * 
 * @author zhaijunfeng
 *
 */
@Configuration
@ConfigurationProperties(prefix = "global.variable", locations = "application.properties")
public class GlobalVariableConf {
	private Boolean isCacheFile;

	private String cacheFileType;

	public Boolean getIsCacheFile() {
		return isCacheFile;
	}

	public void setIsCacheFile(Boolean isCacheFile) {
		this.isCacheFile = isCacheFile;
	}

	public String getCacheFileType() {
		return cacheFileType;
	}

	public void setCacheFileType(String cacheFileType) {
		this.cacheFileType = cacheFileType;
	}
}
