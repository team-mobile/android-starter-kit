package nucleus5.presenter.delivery;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import nucleus5.view.OptionalView;

public class DeliverLatestCache<View, T> implements ObservableTransformer<T, Delivery<View, T>> {

    private final Observable<OptionalView<View>> view;

    public DeliverLatestCache(Observable<OptionalView<View>> view) {
        this.view = view;
    }

    /**
     * 首先调用 Observable.combineLatest,
     * 这个方法可以把两个 Observable 发射的事件, 通过自定的方法合并成一个新事件.
     * 第一个 Observable 就是 view, 第二个是对传入的 observable 进行 materialize 变换,再过滤掉 onComplete 事件;
     * 自定方法是, 如果发射的泛型 View 不为 null ,则返回 Delivery ; 为 null, 就返回 null
     * 最后也是过滤.
     * @param observable
     * @return
     */
    @Override
    public ObservableSource<Delivery<View, T>> apply(Observable<T> observable) {
        return Observable
            .combineLatest(
                view,
                observable
                    .materialize()
                    .filter(new Predicate<Notification<T>>() {
                        @Override
                        public boolean test(Notification<T> notification) throws Exception {
                            return !notification.isOnComplete();
                        }
                    }),
                new BiFunction<OptionalView<View>, Notification<T>, Object[]>() {
                    @Override
                    public Object[] apply(OptionalView<View> view, Notification<T> notification) throws Exception {
                        return new Object[]{view, notification};
                    }
                })
            .concatMap(new Function<Object[], ObservableSource<Delivery<View, T>>>() {
                @Override
                public ObservableSource<Delivery<View, T>> apply(Object[] pack) throws Exception {
                    return Delivery.validObservable((OptionalView<View>) pack[0], (Notification<T>) pack[1]);
                }
            });
    }
}
