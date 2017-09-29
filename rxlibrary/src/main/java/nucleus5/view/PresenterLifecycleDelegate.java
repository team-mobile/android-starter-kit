package nucleus5.view;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import nucleus5.factory.PresenterFactory;
import nucleus5.factory.PresenterStorage;
import nucleus5.presenter.Presenter;

/**
 * 这个类在构造的时候就会构造 ReflectionPresenterFactory, 用的是静态方法.
 * 并且, fragment 并不是直接管理这个 PresenterFactory, 而是把它交给P resenterLifecycleDelegate 管理
 *
 * This class adopts a View lifecycle to the Presenter`s lifecycle.
 *
 * @param <P> a type of the presenter.
 */
public final class PresenterLifecycleDelegate<P extends Presenter> {

    private static final String PRESENTER_KEY = "presenter";
    private static final String PRESENTER_ID_KEY = "presenter_id";

    @Nullable private PresenterFactory<P> presenterFactory;
    @Nullable private P presenter;
    @Nullable private Bundle bundle;

    private boolean presenterHasView;

    public PresenterLifecycleDelegate(@Nullable PresenterFactory<P> presenterFactory) {
        this.presenterFactory = presenterFactory;
    }

    /**
     * {@link ViewWithPresenter#getPresenterFactory()}
     */
    @Nullable
    public PresenterFactory<P> getPresenterFactory() {
        return presenterFactory;
    }

    /**
     * {@link ViewWithPresenter#setPresenterFactory(PresenterFactory)}
     */
    public void setPresenterFactory(@Nullable PresenterFactory<P> presenterFactory) {
        if (presenter != null)
            throw new IllegalArgumentException("setPresenterFactory() should be called before onResume()");
        this.presenterFactory = presenterFactory;
    }

    /**
     * 如果 presenter 为 null,
     * 则通过 factory 构造,
     * 并调用 presenter 的 create 方法
     * 第一次调用 getPresenter 的时候, presenter 才会被创建.
     * 当 presenter 通过 factory 构造出来后,
     * 有个叫 PresenterStorage 的类也会持有这个 presenter 的引用,
     * 并在 presenter 那里注册一个监听, 当 presenter 要被销毁的时候,
     * PresenterStorage 就会知道并也不再持有引用.
     * {@link ViewWithPresenter#getPresenter()}
     */
    public P getPresenter() {
        if (presenterFactory != null) {
            if (presenter == null && bundle != null)
                presenter = PresenterStorage.INSTANCE.getPresenter(bundle.getString(PRESENTER_ID_KEY));

            if (presenter == null) {
                presenter = presenterFactory.createPresenter();
                PresenterStorage.INSTANCE.add(presenter);
                presenter.create(bundle == null ? null : bundle.getBundle(PRESENTER_KEY));
            }
            bundle = null;
        }
        return presenter;
    }

    /**
     * {@link android.app.Activity#onSaveInstanceState(Bundle)}, {@link android.app.Fragment#onSaveInstanceState(Bundle)}, {@link android.view.View#onSaveInstanceState()}.
     */
    public Bundle onSaveInstanceState() {
        Bundle bundle = new Bundle();
        getPresenter();
        if (presenter != null) {
            Bundle presenterBundle = new Bundle();
            presenter.save(presenterBundle);
            bundle.putBundle(PRESENTER_KEY, presenterBundle);
            bundle.putString(PRESENTER_ID_KEY, PresenterStorage.INSTANCE.getId(presenter));
        }
        return bundle;
    }

    /**
     * {@link android.app.Activity#onCreate(Bundle)}, {@link android.app.Fragment#onCreate(Bundle)}, {@link android.view.View#onRestoreInstanceState(Parcelable)}.
     */
    public void onRestoreInstanceState(Bundle presenterState) {
        if (presenter != null)
            throw new IllegalArgumentException("onRestoreInstanceState() should be called before onResume()");
        this.bundle = ParcelFn.unmarshall(ParcelFn.marshall(presenterState));
    }

    /**
     * 在 delegate 的 onResume 方法里,
     * 首先调用 getPresenter 方法, getPresenter完成后,把传入的参数(也就是 view )交给 presenter 保存.
     * 这里可以看出, presenter 最晚会在界面第一次出现的时候被创建
     * {@link android.app.Activity#onResume()},
     * {@link android.app.Fragment#onResume()},
     * {@link android.view.View#onAttachedToWindow()}
     */
    public void onResume(Object view) {
        getPresenter();
        if (presenter != null && !presenterHasView) {
            //noinspection unchecked
            presenter.takeView(view);
            presenterHasView = true;
        }
    }

    /**
     * {@link android.app.Activity#onDestroy()},
     * {@link android.app.Fragment#onDestroyView()},
     * {@link android.view.View#onDetachedFromWindow()}
     */
    public void onDropView() {
        if (presenter != null && presenterHasView) {
            presenter.dropView();
            presenterHasView = false;
        }
    }

    /**
     * {@link android.app.Activity#onDestroy()},
     * {@link android.app.Fragment#onDestroy()},
     * {@link android.view.View#onDetachedFromWindow()}
     */
    public void onDestroy(boolean isFinal) {
        if (presenter != null && isFinal) {
            presenter.destroy();
            presenter = null;
        }
    }
}
