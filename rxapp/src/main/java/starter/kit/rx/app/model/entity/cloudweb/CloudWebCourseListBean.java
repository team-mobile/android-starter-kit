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
public class CloudWebCourseListBean extends DefaultEntity {

    private List<CloudWebCourseBean> cloudWebCourseBeen;

    public List<CloudWebCourseBean> getCloudWebCourseBeen() {
        return cloudWebCourseBeen;
    }

    public void setCloudWebCourseBeen(List<CloudWebCourseBean> cloudWebCourseBeen) {
        this.cloudWebCourseBeen = cloudWebCourseBeen;
    }
}
