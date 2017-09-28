package starter.kit.rx.app.model.entity.xy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.parceler.Parcel;

import starter.kit.model.entity.DefaultEntity;

/**
 * Created by renwoxing on 2017/9/26.
 */
@Parcel
@JsonIgnoreProperties(ignoreUnknown = true)
public class News extends DefaultEntity {

    public int id;
    public String title;
    public String subtitle;
    public String description;
    public String thumbnail;
    @JsonProperty("typeId")
    public int typeid;
    @JsonProperty("typeName")
    public String typename;
    public int creater;
    public int status;
    @JsonProperty("categoryId")
    public int categoryid;
    @JsonProperty("categoryName")
    public String categoryname;
    public int seq;
    @JsonProperty("isDelete")
    public int isdelete;
    @JsonProperty("isTop")
    public int istop;
    @JsonProperty("hasAttachment")
    public int hasattachment;
    @JsonProperty("contentType")
    public int contenttype;
    @JsonProperty("createAt")
    public String createat;
    @JsonProperty("updateAt")
    public String updateat;
    @JsonProperty("deleteAt")
    public String deleteat;
    public String content;


}
