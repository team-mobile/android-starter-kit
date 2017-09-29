package starter.kit.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.MissingBackpressureException;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import starter.kit.model.entity.Entity;
import starter.kit.pagination.PaginatorContract;
import starter.kit.pagination.PaginatorEmitter;
import starter.kit.pagination.PaginatorPresenter;
import starter.kit.rx.R;
import starter.kit.util.RxUtils;
import support.ui.adapters.BaseEasyViewHolderFactory;
import support.ui.adapters.EasyRecyclerAdapter;
import support.ui.adapters.EasyViewHolder;
import support.ui.collect.Lists;

import static starter.kit.util.Utilities.isAdapterEmpty;
import static support.ui.utilities.Objects.isNotNull;

/**
 *
 */
public abstract class StarterRecyclerFragment<E extends Entity, PC extends PaginatorPresenter>
    extends StarterNetworkFragment<PaginatorContract<E>, PC>
    implements com.paginate.Paginate.Callbacks,
    SwipeRefreshLayout.OnRefreshListener {

  SwipeRefreshLayout mSwipeRefreshLayout;
  RecyclerView mRecyclerView;

  private EasyRecyclerAdapter mAdapter;
  private Paginate mPaginate;

  private PaginatorEmitter<E> mPaginatorEmitter;

  public PaginatorEmitter getPaginatorEmitter() {
    return mPaginatorEmitter;
  }

  public EasyRecyclerAdapter getAdapter() {
    return mAdapter;
  }

  public RecyclerView getRecyclerView() {
    return mRecyclerView;
  }

  public void buildFragConfig(StarterFragConfig fragConfig) {
    mPaginatorEmitter = new PaginatorEmitter<>(fragConfig, new Consumer<PaginatorEmitter<E>>() {
      @Override public void accept(@NonNull PaginatorEmitter<E> paginatorEmitter)
          throws Exception {
        getPresenter().requestNext(paginatorEmitter);
      }
    });

    BaseEasyViewHolderFactory viewHolderFactory = fragConfig.getViewHolderFactory();
    if (viewHolderFactory != null) {
      mAdapter.viewHolderFactory(viewHolderFactory);
    }

    //noinspection unchecked
    HashMap<Class, Class<? extends EasyViewHolder>> boundViewHolders = fragConfig.getBoundViewHolders();
    if (!boundViewHolders.isEmpty()) {
      for (Map.Entry<Class, Class<? extends EasyViewHolder>> entry : boundViewHolders.entrySet()) {
        mAdapter.bind(entry.getKey(), entry.getValue());
      }
    }
    // bind empty value

    super.buildFragConfig(fragConfig);
  }

  /**
   * 构造 EasyRecyclerAdapter
   * @see EasyRecyclerAdapter
   * 根据建议, 开发者在这里应该调用 buildFragConfig 方法,
   * 参数为 StarterFragConfig, 建议通过 builder 构造.
   * buildFragConfig 方法会配置 recyclerViewAdapter 的 viewHolderFactory 和 bind,
   * 然后把StarterFragConfig保存起来.
   * @param bundle
   */
  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    mAdapter = new EasyRecyclerAdapter(getContext());
  }

  @Override protected int getFragmentLayout() {
    return R.layout.starter_recycler_view;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(getFragmentLayout(), container, false);
  }

  /**
   * 一, 找到 swipeRefreshLayout 和 RecyclerView
   * 二, 初始化 RecyclerView, 就是通过 fragConfig 设置 layoutManager 等等
   * 三, 这里通过一个叫Paginate的类, 来配置recyclerView的上拉加载更多.
   * 首先判读 fragConfig 的 canAddLoadingListItem
   * (canAddLoadingListItem 就是指需要加载更多的时候,要不要显示一个进度)
   * 如果为 false, 跳过这一步. 用到了 RecyclerPaginate 类
   * @see com.paginate.recycler.RecyclerPaginate
   * @param view
   * @param savedInstanceState
   */
  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mSwipeRefreshLayout = ButterKnife.findById(view, R.id.swipeRefreshLayout);
    mRecyclerView = ButterKnife.findById(view, R.id.supportUiContentRecyclerView);

    setupRecyclerView();
    setupPaginate();
    setupSwipeRefreshLayout();

    if (isNotNull(getFragConfig())) {
      List<Object> items = getFragConfig().getItems();
      if (isNotNull(items) && !items.isEmpty()) {
        mAdapter.addAll(items);
      }
    }
  }

  private void setupSwipeRefreshLayout() {
    if (isNotNull(getFragConfig())) {
      final StarterFragConfig fragConfig = getFragConfig();
      int[] colors = fragConfig.getColorSchemeColors();
      if (colors != null) {
        mSwipeRefreshLayout.setColorSchemeColors(colors);
      }
      boolean enabled = fragConfig.isEnabled();
      mSwipeRefreshLayout.setEnabled(enabled);
      if (enabled) {
        mSwipeRefreshLayout.setOnRefreshListener(this);
      }
    }
  }

  private void setupPaginate() {
    if (isNotNull(getFragConfig())) {
      final StarterFragConfig fragConfig = getFragConfig();
      if (fragConfig.canAddLoadingListItem()) {
        mPaginate = Paginate.with(mRecyclerView, this)
            .setLoadingTriggerThreshold(fragConfig.getLoadingTriggerThreshold())
            .addLoadingListItem(true)
            .setLoadingListItemCreator(fragConfig.getLoadingListItemCreator())
            .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
              @Override public int getSpanSize() {
                return fragConfig.getSpanSizeLookup();
              }
            })
            .build();

        mPaginate.setHasMoreDataToLoad(false);
      }
    }
  }

  private void setupRecyclerView() {
    mRecyclerView.setAdapter(mAdapter);

    if (isNotNull(getFragConfig())) {
      final StarterFragConfig fragConfig = getFragConfig();
      RecyclerView.LayoutManager layoutManager = fragConfig.getLayoutManager();
      if (layoutManager != null) {
        mRecyclerView.setLayoutManager(layoutManager);
      } else {
        mRecyclerView.setLayoutManager(newLayoutManager());
      }

      RecyclerView.ItemDecoration decor = fragConfig.getDecor();
      if (decor != null) {
        mRecyclerView.addItemDecoration(decor);
      }

      RecyclerView.ItemAnimator animator = fragConfig.getAnimator();
      if (animator != null) {
        mRecyclerView.setItemAnimator(animator);
      }
    }
  }

  private RecyclerView.LayoutManager newLayoutManager() {
    return new LinearLayoutManager(getContext());
  }

  @Override public void showProgress() {
    if (!isNotNull(mPaginatorEmitter)) {
      return;
    }
    mPaginatorEmitter.setLoading(true);
    if (isAdapterEmpty(mAdapter)) {
      super.showProgress();
    } else if (isNotNull(mPaginatorEmitter) && mPaginatorEmitter.isFirstPage()) {
      mSwipeRefreshLayout.setRefreshing(true);
    } else if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(true);
    }
  }

  @Override public void hideProgress() {
    if (isNotNull(mPaginatorEmitter)) {
      mPaginatorEmitter.setLoading(false);
    }
    RxUtils.empty(new Action() {
      @Override public void run() {
        if (isNotNull(mSwipeRefreshLayout)) {
          mSwipeRefreshLayout.setRefreshing(false);
        }
      }
    });
  }

  /**
   * <ul>
   *     <li>首先也是判断requestedItems是否为空, 如果为空, 说明是第一次获取,
   *     就会把recyclerViewAdapter清空. (这个功能对应于下拉刷新, 因为下拉刷新肯定要把原来的数据清除). 然后把获取到的数据append进去. </li>
   *     <li> 然后调用mPaginatorEmitter.received(paginatorContract), (这个paginatorContract是对获取的数据的包装).
   * 进去received方法: 先把isLoading设为false, 把paginatorContract保存起来, 然后判断paginatorContract是否为空, 如果为空, 说明已经获得全部数据了, hasMoreData设为false. 如果不为空, 再看如果hasMoreData本身是true, 还是true; 否则就要看获取到的数据等不等于perpage(分页大小), 来判断还有没有更多数据. 最后, 涉及到2个内部变量:firstPaginatorKey和nextPaginatorKey, firstPaginatorKey设为requestItems的第一个的id, nextPaginatorKey设为最后一个的id.这2个变量就是用在PaginatorPresenter的request方法的那2个参数.
   * 总的来看, 这个received方法干了2件事:判断是否获取了全部数据和保存下一页从哪里开始的信息. </li>
   *    <li> mPaginate.setHasMoreDataToLoad(false) 这个方法的参数如果为true, 那就显示那个上拉加载更多的圈圈; 为false就隐藏. </li>
   *    <li> 最后判断现在的recyclerView是不是空的, 如果是空的, 就用contentPresenter显示emptyView.  </li>
   * </ul>
   * @param paginatorContract
   */
  @Override public void onSuccess(PaginatorContract<E> paginatorContract) {
    ArrayList<? extends Entity> items = paginatorContract !=null ? paginatorContract.items() : Lists.newArrayList();
    if (mPaginatorEmitter.isFirstPage()) {
      mAdapter.clear();
    }

    if (items == null) { // handle null
      items = Lists.newArrayList();
    }

    mAdapter.appendAll(items);
    mPaginatorEmitter.received(paginatorContract);

    if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(false);
    }

    if (getContentPresenter() != null) {
      if (isAdapterEmpty(mAdapter)) {
        getContentPresenter().displayEmptyView();
      } else {
        getContentPresenter().displayContentView();
      }
    }
  }

  /**
   * 调用 mPaginatorEmitter.received(null),
   * 这个表示已经获取全部数据
   * @param throwable
   */
  @Override public void onError(Throwable throwable) {
    super.onError(throwable);

    // error handle
    mPaginatorEmitter.received(null);
    if (!(throwable instanceof MissingBackpressureException)) { // 防止重复调用
      // error handle
      mPaginatorEmitter.received(null);
    }

    if (mPaginatorEmitter.isFirstPage() && mAdapter.isEmpty()) {
      mAdapter.clear();
    }

    if (isNotNull(mPaginate)) {
      mPaginate.setHasMoreDataToLoad(false);
    }

    if (isAdapterEmpty(mAdapter)) {
      getContentPresenter().displayErrorView();
    } else {
      getContentPresenter().displayContentView();
    }
  }

  /**
   * 发起网络请求
   * 会调用 {@link PaginatorEmitter#requested()} ()} 来判断,
   * 判断 requestedItems 是否为空,
   * 如果为空,那就开始网络请求.
   * 一开始的时候 requestedItems 肯定是空的,
   * 所以在 onResume 里就会发起第一个网络请求了.
   */
  @Override public void onResume() {
    super.onResume();
    if (isNotNull(mPaginatorEmitter) && !mPaginatorEmitter.requested()) {
      getPresenter().request();
    }
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    mSwipeRefreshLayout = null;
    mRecyclerView = null;
  }

  @Override public void onDestroy() {
    super.onDestroy();
    mAdapter = null;
    mPaginate = null;
  }

  /**
   * 下拉刷新
   * 首先判断是否正在请求, 如果正在请求, 那就显示SwipeRefreshLayout自带的进度条.
   * 如果不是, 那就重置 PaginatorEmitter, 然后进行网络请求.
   */
  @Override public void onRefresh() {
    setErrorResponse(null);
    if (isNotNull(mPaginatorEmitter) && !mPaginatorEmitter.isLoading()) {
      mPaginatorEmitter.reset();
      mPaginatorEmitter.setLoading(true);
      getPresenter().request();
    } else {
      mSwipeRefreshLayout.setRefreshing(true);
    }
  }

  /**
   * 当 recyclerView 滑动到需要加载的位置时, 会调用 isLoading 和 hasLoadedAllItems 这 2 个方法来看要不要发起网络请求,
   * 这2个方法在 StarterRecyclerFragment 实现的.
   * 先看 isLoading 方法: 判断 mPaginatorEmitter 的 isLoading 和 swipeRefreshLayout 的 isRefreshing, 只要其中一个满足, 就会返回true.
   * 这个设计是为了解决 多次滑到底部导致发起多次重复的网络请求 这个问题.
   * 再看 hasLoadedAllItems 方法: 判断 mPaginatorEmitter 的 hasMoreData.
   * 这2个方法都通过, 就到 onLoadMore 方法了, 经过一堆的双保险判断, 就会显示显示那个上拉加载更多的圈圈, 并发起网络请求.
   * 上拉加载更多
   */
  // Paginate delegate
  @Override public void onLoadMore() {
    if (isNotNull(mPaginate) && isNotNull(mPaginatorEmitter)
        && !isAdapterEmpty(mAdapter)
        && mPaginatorEmitter.canRequest()
        && !isLoading()) {
      mPaginate.setHasMoreDataToLoad(true);
      mPaginatorEmitter.request();
    }
  }

  @Override public boolean isLoading() {
    return isNotNull(mSwipeRefreshLayout)
        && isNotNull(mPaginatorEmitter)
        && (mSwipeRefreshLayout.isRefreshing() || mPaginatorEmitter.isLoading());
  }

  @Override public boolean hasLoadedAllItems() {
    return isNotNull(mPaginatorEmitter) && !mPaginatorEmitter.hasMorePages();
  }

  @Override public View provideContentView() {
    return mSwipeRefreshLayout;
  }

  @Override public void onEmptyViewClick(View view) {
    onRefresh();
  }

  @Override public void onErrorViewClick(View view) {
    onRefresh();
  }
}
