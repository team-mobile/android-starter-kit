package starter.kit.rx.app.network.service;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import starter.kit.pagination.Paginator;
import starter.kit.rx.app.model.entity.xy.News;


/**
 * Created by renwoxing on 2017/9/26.
 */

public interface NewsService {

    @GET("/news/getXYNews")
    Observable<Paginator<News>> fetchNewsWithPage(
            @Query("pageIndex") int pageIndex,
            @Query("pageSize") int pageSize);
}
