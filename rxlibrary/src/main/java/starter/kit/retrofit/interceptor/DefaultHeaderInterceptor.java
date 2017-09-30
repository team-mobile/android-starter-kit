package starter.kit.retrofit.interceptor;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import starter.kit.account.AccountManager;
import support.ui.utilities.Strings;

import static starter.kit.model.config.Constant.BASE_URL_HEADER_KEY;

/**
 * @author rener
 */
public class DefaultHeaderInterceptor implements HeaderInterceptor {

    private Headers.Builder builder;

    public DefaultHeaderInterceptor(Headers.Builder builder) {
        this.builder = builder;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        if (builder == null) {
            // builder = new Headers.Builder();
            builder = originalRequest.headers().newBuilder();
        }

        final String token = AccountManager.INSTANCE.token();
        if (!Strings.isBlank(token)) {
            builder.set("Authorization", "Bearer " + token);
        }

        String baseUrl = originalRequest.header(BASE_URL_HEADER_KEY);
        if (!TextUtils.isEmpty(baseUrl)) {
            Request.Builder oldBuilder = originalRequest.newBuilder();
            oldBuilder.removeHeader(BASE_URL_HEADER_KEY);
            HttpUrl newBaseUrl = HttpUrl.parse(baseUrl);
            //从request中获取原有的HttpUrl实例oldHttpUrl
            HttpUrl oldHttpUrl = originalRequest.url();
            //重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme(newBaseUrl.scheme())
                    .host(newBaseUrl.host())
                    .port(newBaseUrl.port())
                    .build();
            //重建这个request，通过builder.url(newFullUrl).build()；
            //然后返回一个response至此结束修改
            return chain.proceed(oldBuilder.url(newFullUrl).headers(builder.build()).build());
        }
        Request compressedRequest = originalRequest
                .newBuilder()
                .headers(builder.build())
                .build();

        return chain.proceed(compressedRequest);
    }


}
