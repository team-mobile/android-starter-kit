package starter.kit.pagination;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import starter.kit.app.StarterFragConfig;
import starter.kit.model.entity.Entity;
import support.ui.collect.Lists;


public class PaginatorEmitter<E extends Entity> implements Emitter<E>, PaginatorContract<E> {

  private final StarterFragConfig mFragConfig;
  private Consumer<PaginatorEmitter<E>> onRequest;

  private boolean hasMoreData;
  private boolean isLoading;
  private PaginatorContract mPaginatorContract;

  private int currentPage;
  private String firstPaginatorKey;
  private String nextPaginatorKey;

  private ArrayList<E> requestedItems = Lists.newArrayList();  //重要变量，从第一个id/第一页开始, 网络请求获取到的所有数据.

  /**
   * <ul>
   *     <li>把fragConfig保存起来, 内部变量currentPage等于fragConfig.getStartPage(),这个默认值是1 </li>
   *     <li>onRequest保存起来, 内部变量hasMoreData和isLoading设为true  </li>
   *     <li>通过fragConfig判断是通过分页还是key获取数据, 这里涉及到2个内部变量:String firstPaginatorKey, nextPaginatorKey. 如果是分页, 这2个变量为currentPage; 如果是key, 那就为null </li>
   * <ul/>
   * @param fragConfig
   * @param onRequest
   */
  public PaginatorEmitter(StarterFragConfig fragConfig, Consumer<PaginatorEmitter<E>> onRequest) {
    this.mFragConfig = fragConfig;
    this.currentPage = fragConfig.getStartPage();

    resetPaginatorKey();

    this.onRequest = onRequest;
    this.hasMoreData = true;
    this.isLoading = true;
  }

  private void resetPaginatorKey() {
    if (!mFragConfig.withKeyRequest()) {
      firstPaginatorKey = String.valueOf(currentPage);
      nextPaginatorKey = String.valueOf(currentPage);
    } else {
      firstPaginatorKey = null;
      nextPaginatorKey = null;
    }
  }

  @Override public void received(PaginatorContract<E> paginatorContract) {
    isLoading = false;
    mPaginatorContract = paginatorContract;
    if (paginatorContract == null) {
      resetPaginatorKey();
      hasMoreData = false;
      return;
    }

    if (paginatorContract.isEmpty()) {
      resetPaginatorKey();
      hasMoreData = false;
      return;
    }

    requestedItems.addAll(items());

    hasMoreData = mPaginatorContract.hasMorePages() || paginatorContract.size() >= perPage();

    if (mFragConfig.withKeyRequest()) {
      firstPaginatorKey = requestedItems.get(0).paginatorKey();
      nextPaginatorKey = lastItem().paginatorKey();
    } else {
      firstPaginatorKey = String.valueOf(mFragConfig.getStartPage());
      currentPage = mPaginatorContract.currentPage() + 1;
      nextPaginatorKey = String.valueOf(currentPage);
    }
  }

  @Override public void reset() {
    requestedItems.clear();
    currentPage = mFragConfig.getStartPage();
    resetPaginatorKey();
    isLoading = false;
    hasMoreData = true;
  }

  @Override public boolean isFirstPage() {
    return requestedItems.isEmpty();
  }

  @Override public void request() {
    if (canRequest()) {
      isLoading = true;
      try {
        onRequest.accept(this);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override public boolean requested() {
    return !requestedItems.isEmpty();
  }

  @Override public boolean canRequest() {
    return hasMorePages() && !isLoading;
  }

  @Override public boolean isLoading() {
    return isLoading;
  }

  @Override public void setLoading(boolean isLoading) {
    this.isLoading = isLoading;
  }

  @Override public ArrayList<E> items() {
    //noinspection unchecked
    ArrayList<E> items = mPaginatorContract.items();
    return items != null ? items : Lists.newArrayList();
  }

  @Override public E firstItem() {
    //noinspection unchecked
    return (E) mPaginatorContract.firstItem();
  }

  @Override public E lastItem() {
    //noinspection unchecked
    return (E) mPaginatorContract.lastItem();
  }

  @Override public int perPage() {
    return mFragConfig.getPageSize();
  }

  @Override public int currentPage() {
    return currentPage;
  }

  @Override public String firstPaginatorKey() {
    return firstPaginatorKey;
  }

  @Override public String nextPaginatorKey() {
    return nextPaginatorKey;
  }

  @Override public int total() {
    return mPaginatorContract != null ? mPaginatorContract.total() : 0;
  }

  @Override public int size() {
    return mPaginatorContract != null ? mPaginatorContract.size() : 0;
  }

  @Override public boolean hasMorePages() {
    if (mPaginatorContract != null) {
      return mPaginatorContract.hasMorePages() || hasMoreData;
    }
    return hasMoreData;
  }

  @Override public boolean isEmpty() {
    return mPaginatorContract != null && mPaginatorContract.isEmpty();
  }
}
