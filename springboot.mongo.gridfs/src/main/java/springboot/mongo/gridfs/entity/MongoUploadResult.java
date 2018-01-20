package springboot.mongo.gridfs.entity;

import java.util.List;

/**
 * @author zhaijunfeng
 */
public class MongoUploadResult {

    private Boolean             success;
    private List<MongoFileInfo> files;
    private String              msg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<MongoFileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<MongoFileInfo> files) {
        this.files = files;
    }
}
