
/**
 * @author zhaijunfeng
 */
package springboot.mongo.gridfs.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
/**
 * 用于原生写法，主要获取连接信息
 * @author zhaijunfeng
 *
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb", locations = "application.properties")
public class MongoGridFsConf {

    private String username;

    private String password;

    private String database;

    private String host;

    private int    port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

}
