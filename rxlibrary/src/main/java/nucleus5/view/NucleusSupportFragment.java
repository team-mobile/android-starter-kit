package nucleus5.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import nucleus5.factory.PresenterFactory;
import nucleus5.factory.ReflectionPresenterFactory;
import nucleus5.presenter.Presenter;

/**
 * NucleusSupportFragment 连接了view 和 presenter,
 * 当 activity 处于不可交互状态时, presenter 不再持有 view.
 * 而且解决了activity的Configurations变化时,presenter反复构造的问题.
 * This view is an example of how a view should control it's presenter.
 * You can inherit from this class or copy/paste this class's code to
 * create your own view implementation.
 *
 * @param <P> a type of presenter to return with {@link #getPresenter}.
 */
public abstract class NucleusSupportFragment<P extends Presenter> extends Fragment implements ViewWithPresenter<P> {

    private static final String PRESENTER_STATE_KEY = "presenter_state";
    private PresenterLifecycleDelegate<P> presenterDelegate =
        new PresenterLifecycleDelegate<>(ReflectionPresenterFactory.<P>fromViewClass(getClass()));

    /**
     * Returns a current presenter factory.
     */
    public PresenterFactory<P> getPresenterFactory() {
        return presenterDelegate.getPresenterFactory();
    }

    /**
     * Sets a presenter factory.
     * Call this method before onCreate/onFinishInflate to override default {@link ReflectionPresenterFactory} presenter factory.
     * Use this method for presenter dependency injection.
     */
    @Override
    public void setPresenterFactory(PresenterFactory<P> presenterFactory) {
        presenterDelegate.setPresenterFactory(presenterFactory);
    }

    /**
     * Returns a current attached presenter.
     * This method is guaranteed to return a non-null value between
     * onResume/onPause and onAttachedToWindow/onDetachedFromWindow calls
     * if the presenter factory returns a non-null value.
     *
     * @return a currently attached presenter or null.
     */
    public P getPresenter() {
        return presenterDelegate.getPresenter();
    }

    /**
     * 如果 bundle 不为 null,
     * 则通过 bundle 恢复 presenter
     * @param bundle
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null)
            presenterDelegate.onRestoreInstanceState(bundle.getBundle(PRESENTER_STATE_KEY));
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBundle(PRESENTER_STATE_KEY, presenterDelegate.onSaveInstanceState());
    }

    /**
     * 就是调用了presenterDelegate 的 onResume 方法,
     * 参数为fragment自己,
     */
    @Override
    public void onResume() {
        super.onResume();
        presenterDelegate.onResume(this);
    }

    /**
     * 调用 delegate 的 onDropView 方法,
     * 就是让 presenter 不再持有 view 的引用, view=null
     */
    @Override
    public void onPause() {
        super.onPause();
        presenterDelegate.onDropView();
    }

    /**
     * 也是调用 delegate 的 onDestroy 方法
     * 这个方法先看是不是 activity 要重启(例如横竖屏转换),
     * 如果 activity 不是重启而是真的要 destroy 了,
     * presenter 就会调用上面提到的那个监听,
     * 然后 delegate 放弃对 presenter 的引用(presenter = null).
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        presenterDelegate.onDestroy(!getActivity().isChangingConfigurations());
    }
}
