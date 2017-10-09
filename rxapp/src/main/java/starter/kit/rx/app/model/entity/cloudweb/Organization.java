package starter.kit.rx.app.model.entity.cloudweb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.parceler.Parcel;

import starter.kit.model.entity.DefaultEntity;


@Parcel
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization extends DefaultEntity {

    private Long id;
    private int type;
    private int templateId;
    private String orgName;
    private Long refId;
    private Long createTime;
    public void setId(Long id) {
        this.id = id;
    }
    public Long getId() {
        return id;
    }

    public void setType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }

    public void setTemplateid(int templateid) {
        this.templateId = templateid;
    }
    public int getTemplateid() {
        return templateId;
    }

    public void setOrgname(String orgname) {
        this.orgName = orgname;
    }
    public String getOrgname() {
        return orgName;
    }

    public void setRefid(Long refid) {
        this.refId = refid;
    }
    public Long getRefid() {
        return refId;
    }

    public void setCreatetime(Long createtime) {
        this.createTime = createtime;
    }
    public Long getCreatetime() {
        return createTime;
    }
}
