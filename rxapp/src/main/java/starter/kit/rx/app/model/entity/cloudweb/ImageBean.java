package starter.kit.rx.app.model.entity.cloudweb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.parceler.Parcel;

/**
 * Created by renwoxing on 2017/7/6.
 */

@Parcel
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImageBean {
    private int id;

    private String courseId;

    private int platform;

    private int type;

    private int seq;

    private String path;

    private String remark;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setCourseId(String courseId){
        this.courseId = courseId;
    }
    public String getCourseId(){
        return this.courseId;
    }
    public void setPlatform(int platform){
        this.platform = platform;
    }
    public int getPlatform(){
        return this.platform;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public void setSeq(int seq){
        this.seq = seq;
    }
    public int getSeq(){
        return this.seq;
    }
    public void setPath(String path){
        this.path = path;
    }
    public String getPath(){
        return this.path;
    }
    public void setRemark(String remark){
        this.remark = remark;
    }
    public String getRemark(){
        return this.remark;
    }
}
