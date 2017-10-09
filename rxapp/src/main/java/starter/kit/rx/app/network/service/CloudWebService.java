package starter.kit.rx.app.network.service;


import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import starter.kit.pagination.LengthAwarePaginator;
import starter.kit.rx.app.model.entity.cloudweb.CloudWebCourseBean;
import starter.kit.rx.app.model.entity.cloudweb.Organization;

public interface CloudWebService {

    //

    @GET("/cloudWeb/courseList")
    Observable<LengthAwarePaginator<CloudWebCourseBean>> courseList(
            @Query("schoolId") String maxId);

    @GET("/cloudWeb/orgList")
    Observable<List<Organization>> orgList();
}
