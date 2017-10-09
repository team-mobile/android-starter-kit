package starter.kit.rx.app.model.entity.cloudweb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.parceler.Parcel;

/**
 * Created by renwoxing on 2017/7/6.
 */
@Parcel
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolBean {
    private int id;

    private String courseId;

    private int schoolId;

    private String schoolName;

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
    public void setSchoolId(int schoolId){
        this.schoolId = schoolId;
    }
    public int getSchoolId(){
        return this.schoolId;
    }
    public void setSchoolName(String schoolName){
        this.schoolName = schoolName;
    }
    public String getSchoolName(){
        return this.schoolName;
    }
}
