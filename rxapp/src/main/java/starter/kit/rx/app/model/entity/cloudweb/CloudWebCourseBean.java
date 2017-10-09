package starter.kit.rx.app.model.entity.cloudweb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.parceler.Parcel;

import java.util.List;

import starter.kit.model.entity.DefaultEntity;

/**
 * Created by renwoxing on 2017/7/6.
 */
@Parcel
@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudWebCourseBean extends DefaultEntity {

    private List<ImageBean> image ;

    private boolean isSubscribed;

    private List<SchoolBean> school ;

    private String name;

    private int type;

    private int updateTime;

    private int id;

    public void setImage(List<ImageBean> image){
        this.image = image;
    }
    public List<ImageBean> getImage(){
        return this.image;
    }
    public void setIsSubscribed(boolean isSubscribed){
        this.isSubscribed = isSubscribed;
    }
    public boolean getIsSubscribed(){
        return this.isSubscribed;
    }
    public void setSchool(List<SchoolBean> school){
        this.school = school;
    }
    public List<SchoolBean> getSchool(){
        return this.school;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public void setUpdateTime(int updateTime){
        this.updateTime = updateTime;
    }
    public int getUpdateTime(){
        return this.updateTime;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }

    @Override
    public String toString() {
        return "CloudWebCourseBean{" +
                "image=" + image +
                ", isSubscribed=" + isSubscribed +
                ", school=" + school +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", updateTime=" + updateTime +
                ", id=" + id +
                '}';
    }
}
