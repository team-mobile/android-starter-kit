package starter.kit.rx.app.feature.feed;

import android.os.Bundle;

import com.orhanobut.logger.Logger;

import io.reactivex.Observable;
import starter.kit.pagination.Paginator;
import starter.kit.pagination.PaginatorPresenter;
import starter.kit.rx.app.model.entity.xy.News;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.NewsService;

/**
 * Created by renwoxing on 2017/9/26.
 */

public class NewsPresenter extends PaginatorPresenter<Paginator<News>> {

    private NewsService mNewsService;

    @Override protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mNewsService = ApiService.createXYNewsService();
    }
    @Override
    public Observable<Paginator<News>> request(String firstPaginatorKey, String nextPaginatorKey, int perPage) {
        return mNewsService.fetchNewsWithPage(1,10);
    }

    @Override public int restartableId() {
        Logger.d("NewsPresenter hashCode : %s, this: %s", super.hashCode(),this.hashCode());
        return hashCode();
    }
}
