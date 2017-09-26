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

    @JsonProperty("id") public String id;
    public String title;
    public String content;
}
