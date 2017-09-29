package starter.kit.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class DefaultEntity implements Entity {

  @JsonProperty("id") public String identifier;

  @Override public String identifier() {
    return identifier;
  }

  @Override public String paginatorKey() {
    return identifier;
  }
}
