package starter.kit.rx.app.network.service;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import starter.kit.rx.app.BuildConfig;
import starter.kit.rx.app.model.entity.User;

import static starter.kit.model.config.Constant.BASE_URL_HEADER_KEY;

/**
 * Created by YuGang Yang on 06 29, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public interface AuthService {

    /**
     * 登录接口
     *
     * @param phone    手机号码
     * @param password 密码
     * @return Call
     */
    @Headers(BASE_URL_HEADER_KEY + ":" + BuildConfig.API_AUTH_HOST)
    @FormUrlEncoded
    @POST("/auth/authorize")
    Observable<User>
    login(@Field("account") String phone,
          @Field("password") String password,
          @Field("role") String role,
          @Field("platform") String platform);
}
