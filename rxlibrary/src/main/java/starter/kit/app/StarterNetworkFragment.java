package starter.kit.app;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import io.reactivex.functions.Action;
import nucleus5.presenter.Presenter;
import starter.kit.retrofit.ErrorResponse;
import starter.kit.rx.R;
import starter.kit.util.ErrorHandler;
import starter.kit.util.NetworkContract;
import starter.kit.util.RxUtils;
import support.ui.content.ContentPresenter;
import support.ui.content.EmptyView;
import support.ui.content.ErrorView;
import support.ui.content.ReflectionContentPresenterFactory;
import support.ui.content.RequiresContent;

/**
 * 在 StarterNetworkFragment 构造的时候,
 * 会从注解中读取 3 个 class,
 * 然后构造一个 contentPresenterFactory,
 * 这个 factory 也很简单, 就是在需要的时候 new 一个 contentPresenter.
 */
@RequiresContent
public abstract class StarterNetworkFragment<T, P extends Presenter>
        extends StarterFragment<P>
        implements NetworkContract.ContentInterface<T>,
        EmptyView.OnEmptyViewClickListener,
        ErrorView.OnErrorViewClickListener {

    private ReflectionContentPresenterFactory factory =
            ReflectionContentPresenterFactory.fromViewClass(getClass());
    private ContentPresenter contentPresenter;

    private StarterFragConfig mFragConfig;

    private ErrorResponse mErrorResponse;

    /**
     * 构造一个 contentPresenter,
     * 然后调用 contentPresenter 的 onCreate(Context),
     * {@link ContentPresenter#onCreate(Context)}
     * contentPresenter 会把 context 保存起来.
     * 然后还会执行
     * @param bundle
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        contentPresenter = factory.createContentPresenter();
        contentPresenter.onCreate(getContext());

        //当用户点击这个 View 的时候应该做什么,
        // 一般就是刷新一下.
        // starterNetworkFragment 并没有重写这 2 个方法, 所以它是 abstract 的, 需要继承者重写.
        contentPresenter.setOnEmptyViewClickListener(this);
        contentPresenter.setOnErrorViewClickListener(this);
    }

    protected void buildFragConfig(StarterFragConfig fragConfig) {
        mFragConfig = fragConfig;
    }

    /**
     * 这里会把 fragment 布局的 rootview 和 contentView 给 contentPresenter 保存起来.
     * 这个 contentView 就是指你的布局的最外层的 View, 需要你来提供.
     */
    @Override
    public void onResume() {
        super.onResume();
        contentPresenter.attachContainer(provideContainer());
        contentPresenter.attachContentView(provideContentView());
    }

    @Override
    public void onPause() {
        super.onPause();
        contentPresenter.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        contentPresenter.onDestroy();
        contentPresenter = null;
    }

    public ContentPresenter getContentPresenter() {
        return contentPresenter;
    }

    public StarterFragConfig getFragConfig() {
        return mFragConfig;
    }


    /**
     * showProgress 调用了
     * contentPresenter 的 displayLoadView 方法
     * {@link ContentPresenter#displayLoadView()}
     * 加载中
     */
    @Override
    public void showProgress() {
        RxUtils.empty(new Action() {
            @Override
            public void run() {
                getContentPresenter().displayLoadView();
            }
        });
    }

    @Override
    public void hideProgress() {

    }

    /**
     * 实现 NetworkContract.View 接口
     * getContentPresenter().buildErrorTitle 方法,
     * 传入 retrofitException 的 message.
     * 这个方法就要用到构造时传入的 errorViewClass了,
     * contentPresenter 会通过反射来构造,
     * 注意这个 errorViewClass 要 implement ErrorView 这个接口,
     * contentPresenter 然后会调用这个 errorView 的一些方法.
     * @param throwable
     */
    @Override
    public void onError(Throwable throwable) {
        mErrorResponse = ErrorHandler.handleThrowable(throwable);
        if (mErrorResponse != null) {
            getContentPresenter().buildErrorSubtitle(mErrorResponse.getMessage());
            getContentPresenter().buildErrorTitle(String.valueOf(mErrorResponse.getStatusCode()));
            getContentPresenter().buildErrorImageView(R.drawable.error);
        } else {
            getContentPresenter().buildErrorSubtitle(throwable.getMessage());
            getContentPresenter().buildErrorTitle(R.string.starter_error_title_some_question_placeholder);
            getContentPresenter().buildErrorImageView(R.drawable.error);
        }
        if (throwable instanceof IOException) {
            getContentPresenter().buildErrorImageView(R.drawable.support_ui_network_error);
        }
    }

    @Override
    public void onSuccess(T data) {
    }

    public ViewGroup provideContainer() {
        return (ViewGroup) getView();
    }

    public abstract View provideContentView();

    public ErrorResponse getErrorResponse() {
        return mErrorResponse;
    }

    public void setErrorResponse(ErrorResponse errorResponse) {
        mErrorResponse = errorResponse;
    }
}
