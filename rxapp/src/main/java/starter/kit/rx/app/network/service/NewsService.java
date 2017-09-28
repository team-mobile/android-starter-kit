package starter.kit.rx.app.network.service;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import starter.kit.pagination.Paginator;
import starter.kit.rx.app.model.entity.xy.News;

import static starter.kit.model.config.Constant.BASE_URL_HEADER_KEY;
import static starter.kit.rx.app.Profile.JX_API_BASE_URL;

/**
 * Created by renwoxing on 2017/9/26.
 */

public interface NewsService {

    @Headers(BASE_URL_HEADER_KEY + ":" + JX_API_BASE_URL)
    @GET("/news/getXYNews")
    Observable<Paginator<News>> fetchNewsWithPage(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize);
}
