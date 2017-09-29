package starter.kit.pagination;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import starter.kit.model.entity.Entity;
import support.ui.collect.Lists;

/**
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true) public class Paginator<T extends Entity>
    extends AbstractPaginator<T> {

  @JsonProperty("totals") private int total;

  @JsonProperty("pageSize") private int perPage;
  @JsonProperty("pages") private int currentPage;
  @JsonProperty("pageNumber") private int lastPage;

  @JsonProperty("rows") private ArrayList<T> items;

  @Override public ArrayList<T> items() {
    return this.items == null ? Lists.newArrayList() : this.items;
  }

  @Override public int currentPage() {
    return currentPage;
  }

  @Override public int perPage() {
    return perPage;
  }

  @Override public int size() {
    return items != null ? items.size() : 0;
  }

  @Override public int total() {
    return total;
  }

  @Override public boolean hasMorePages() {
    return currentPage < lastPage && size() >= perPage();
  }

  @Override public boolean isEmpty() {
    return size() == 0;
  }
}
