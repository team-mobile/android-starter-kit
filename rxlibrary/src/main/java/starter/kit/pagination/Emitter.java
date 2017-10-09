package starter.kit.pagination;

import starter.kit.model.entity.Entity;


public interface Emitter<E extends Entity> {

  boolean isFirstPage();

  void received(PaginatorContract<E> paginatorContract);

  void reset();

  void request();

  boolean canRequest();

  boolean requested();

  boolean isLoading();

  void setLoading(boolean isLoading);
}
