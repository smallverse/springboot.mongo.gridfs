package springboot.mongo.gridfs.entity;

/**
 * @author zhaijunfeng
 */
public class MongoFileInfo {

	private String id;
	private String name;
	private String serverPath;

	public MongoFileInfo(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public MongoFileInfo(String id, String name, String serverPath) {
		this.id = id;
		this.name = name;
		this.serverPath = serverPath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 服务端缓存路径
	 * 
	 * @return
	 */
	public String getServerPath() {
		return serverPath;
	}

	/**
	 * 服务端缓存路径
	 * 
	 * @return
	 */
	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

}
