package support.ui.content;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Constructor;
import support.ui.utilities.LayoutHelper;
import support.ui.widget.R;

/**
 * 独立类，
 * 没有继承 Presenter 基类
 * 负责显示加载中,
 * 显示网络错误,
 * 显示空白
 * ContentPresenter
 * 是通过注解（ RequiresContent ）获取到参数,
 * 然后构造。
 */
public final class ContentPresenter {

  private static final int ID_NONE = -1;
  private static final int LoadViewId = R.id.supportUiLoadView;
  private static final int EmptyViewId = R.id.supportUiEmptyView;
  private static final int ErrorViewId = R.id.supportUiErrorView;
  private static final int ContentViewId = R.id.supportUiContentView;

  private SparseArrayCompat<Class<View>> mViewClassArray = new SparseArrayCompat<>(4);
  private SparseArrayCompat<View> mViewArray = new SparseArrayCompat<>(4);
  int mCurrentId = ID_NONE;
  ViewGroup mContainer;
  View mContentView;
  Context mContext;

  private EmptyView.OnEmptyViewClickListener onEmptyViewClickListener;
  private ErrorView.OnErrorViewClickListener onErrorViewClickListener;

  /**
   * 传入 3 个继承 view 的 Class 参数,
   * 传入之后, 把这 3 个保存起来.
   * @param loadViewClass
   * @param emptyViewClass
   * @param errorViewClass
   */
  public ContentPresenter(Class<View> loadViewClass, Class<View> emptyViewClass, Class<View> errorViewClass) {
    buildViewClassArray(loadViewClass, emptyViewClass, errorViewClass);
  }

  public ContentPresenter onCreate(Context context) {
    mContext = context;
    return this;
  }

  public ContentPresenter attachContainer(ViewGroup container) {
    mContainer = container;
    return this;
  }

  public ContentPresenter attachContentView(View contentView) {
    mContentView = contentView;
    return this;
  }

  public void setOnEmptyViewClickListener(EmptyView.OnEmptyViewClickListener listener) {
    this.onEmptyViewClickListener = listener;
  }

  public void setOnErrorViewClickListener(
      ErrorView.OnErrorViewClickListener onErrorViewClickListener) {
    this.onErrorViewClickListener = onErrorViewClickListener;
  }

  public void onDestroyView() {
    mCurrentId = ID_NONE;
    mContentView = null;
    mViewArray.clear();
  }

  public void onDestroy() {
    onEmptyViewClickListener = null;
    onErrorViewClickListener = null;
    mContext = null;
    mContainer = null;
    mViewClassArray = null;
    mViewArray = null;
  }

  /**
   * 显示进度条
   * contentPresenter 会先检查是否已经展示了 loadView.
   */
  public ContentPresenter displayLoadView() {
    final int loadViewId = LoadViewId;
    if (mCurrentId != loadViewId) {
      displayView(loadViewId);
    }
    return this;
  }

  /**
   * 显示空白页
   */
  public ContentPresenter displayEmptyView() {
    final int emptyViewId = EmptyViewId;
    if (mCurrentId != emptyViewId) {
      View view = displayView(emptyViewId);
      if (view instanceof EmptyView) {
        EmptyView emptyView = (EmptyView) view;
        emptyView.setOnEmptyViewClickListener(onEmptyViewClickListener);
      }
    }
    return this;
  }

  /**
   * 显示错误页面
   */
  public ContentPresenter displayErrorView() {
    final int errorViewId = ErrorViewId;
    if (mCurrentId != errorViewId) {
      View view = displayView(errorViewId);
      if (view instanceof ErrorView) {
        ErrorView errorView = (ErrorView) view;
        errorView.setOnErrorViewClickListener(onErrorViewClickListener);
      }
    }
    return this;
  }

  /**
   * 显示内容
   */
  public ContentPresenter displayContentView() {
    final int contentViewId = ContentViewId;
    if (mCurrentId != contentViewId && mContentView != null) {
      final ViewGroup container = mContainer;
      final ViewGroup.LayoutParams layoutParams = LayoutHelper.createViewGroupLayoutParams();
      container.removeAllViews();
      container.addView(mContentView, layoutParams);
      mCurrentId = contentViewId;
    }
    return this;
  }

  // empty start
  public ContentPresenter buildEmptyImageView(@DrawableRes int drawableRes) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.buildEmptyImageView(drawableRes);
    }
    return this;
  }

  public ContentPresenter buildEmptyTitle(@StringRes int stringRes) {
    return buildEmptyTitle(mContext.getResources().getString(stringRes));
  }

  public ContentPresenter buildEmptyTitle(String title) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.buildEmptyTitle(title);
    }
    return this;
  }

  public ContentPresenter buildEmptySubtitle(@StringRes int stringRes) {
    return buildEmptySubtitle(mContext.getResources().getString(stringRes));
  }

  public ContentPresenter buildEmptySubtitle(String subtitle) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.buildEmptySubtitle(subtitle);
    }
    return this;
  }

  public ContentPresenter shouldDisplayEmptySubtitle(boolean display) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.shouldDisplayEmptySubtitle(display);
    }
    return this;
  }

  public ContentPresenter shouldDisplayEmptyTitle(boolean display) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.shouldDisplayEmptyTitle(display);
    }
    return this;
  }

  public ContentPresenter shouldDisplayEmptyImageView(boolean display) {
    View view = checkView(EmptyViewId);
    if (view instanceof EmptyView) {
      EmptyView emptyView = (EmptyView) view;
      emptyView.shouldDisplayEmptyImageView(display);
    }
    return this;
  }

  // error start
  public ContentPresenter buildErrorImageView(@DrawableRes int drawableRes) {
    View view = checkView(ErrorViewId);
    if (view instanceof ErrorView) {
      ErrorView errorView = (ErrorView) view;
      errorView.buildErrorImageView(drawableRes);
    }
    return this;
  }

  public ContentPresenter buildErrorTitle(@StringRes int stringRes) {
    return buildErrorTitle(mContext.getResources().getString(stringRes));
  }

  public ContentPresenter buildErrorTitle(String title) {
    View view = checkView(ErrorViewId);
    if (view instanceof ErrorView) {
      ErrorView errorView = (ErrorView) view;
      errorView.buildErrorTitle(title);
    }
    return this;
  }

  public ContentPresenter buildErrorSubtitle(@StringRes int stringRes) {
    return buildErrorSubtitle(mContext.getResources().getString(stringRes));
  }

  public ContentPresenter buildErrorSubtitle(String subtitle) {
    View view = checkView(ErrorViewId);
    if (view instanceof ErrorView) {
      ErrorView errorView = (ErrorView) view;
      errorView.buildErrorSubtitle(subtitle);
    }
    return this;
  }

  public ContentPresenter shouldDisplayErrorSubtitle(boolean display) {
    View view = checkView(ErrorViewId);
    if (view instanceof ErrorView) {
      ErrorView errorView = (ErrorView) view;
      errorView.shouldDisplayErrorSubtitle(display);
    }
    return this;
  }

  public ContentPresenter shouldDisplayErrorTitle(boolean display) {
    View view = checkView(ErrorViewId);
    if (view instanceof ErrorView) {
      ErrorView errorView = (ErrorView) view;
      errorView.shouldDisplayErrorTitle(display);
    }
    return this;
  }

  public ContentPresenter shouldDisplayErrorImageView(boolean display) {
    View view = checkView(ErrorViewId);
    if (view instanceof ErrorView) {
      ErrorView errorView = (ErrorView) view;
      errorView.shouldDisplayErrorImageView(display);
    }
    return this;
  }

  /**
   * 通过删除 container 的内容,
   * 再把 viewId 添加上去, 这样来显示 View.
   * @param viewId
   * @return
   */
  private View displayView(@IdRes int viewId) {
    // container 指最外层的 view
    final ViewGroup container = mContainer;
    // 通过 viewId 找 view, 可以看成单例
    final View view = checkView(viewId);
    // 宽和高都是 match_parent
    final ViewGroup.LayoutParams layoutParams = LayoutHelper.createViewGroupLayoutParams();
    container.removeAllViews();

    if (view != null) { //  <- fix The specified child already has a parent. You must call removeView() on the child's parent first.
      ViewGroup parent = (ViewGroup) view.getParent();
      if (parent != null) {
        parent.removeView(view);
      }
    }
    try {
      container.addView(view, layoutParams);
    }catch (Exception e){}
    mCurrentId = viewId;
    return view;
  }

  @Nullable private View checkView(int viewId) {
    final SparseArrayCompat<View> viewArray = mViewArray;
    View view = viewArray.get(viewId);
    if (view == null) {
      view = buildView(viewId);
      viewArray.put(viewId, view);
    }
    return view;
  }

  private ContentPresenter buildViewClassArray(Class<View> loadViewClass,
      Class<View> emptyViewClass, Class<View> errorViewClass) {
    final SparseArrayCompat<Class<View>> viewClassArray = mViewClassArray;
    viewClassArray.put(LoadViewId, loadViewClass);
    viewClassArray.put(EmptyViewId, emptyViewClass);
    viewClassArray.put(ErrorViewId, errorViewClass);
    return this;
  }

  @Nullable private View buildView(int viewId) {
    final SparseArrayCompat<Class<View>> viewClassArray = mViewClassArray;
    final Class<View> viewClass = viewClassArray.get(viewId);
    try {
      Constructor<? extends View> constructor = viewClass.getDeclaredConstructor(Context.class);
      final Context context = mContext;
      return constructor.newInstance(context);
    } catch (Throwable e) {
      throw new RuntimeException(
          "Unable to create View for" + viewClass + ". " + e.getLocalizedMessage(), e);
    }
  }
}
