package starter.kit.retrofit.interceptor;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import starter.kit.model.entity.TokenEntity;
import starter.kit.rx.BuildConfig;

import static starter.kit.model.config.Constant.BASE_URL_HEADER_KEY;

/**
 * RefreshTokenService
 * <p>
 * 重新获取 token api service
 * 使用:
 * <code>
 * </code>
 * <p>
 * Created by renwoxing on 2017/9/30.
 */
public interface RefreshTokenService {

    @Headers(BASE_URL_HEADER_KEY + ":" + BuildConfig.API_AUTH_HOST)
    @FormUrlEncoded
    @POST("/auth/refreshToken")
    Call<TokenEntity>
    refreshToken(@Field("origin_token") String origin_token,
                 @Field("refresh_token") String refresh_token);
}
