package starter.kit.retrofit;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import starter.kit.retrofit.error.RxErrorHandlingCallAdapterFactory;
import starter.kit.retrofit.interceptor.DefaultHeaderInterceptor;
import starter.kit.retrofit.interceptor.TokenHandlerInterceptor;
import starter.kit.retrofit.interceptor.SignHeaderInterceptor;
import support.ui.app.AppInfo;
import support.ui.app.SupportApp;
import support.ui.utilities.Preconditions;
import support.ui.utilities.Strings;

import static starter.kit.model.config.Constant.TIMEOUT_CONNECTION;
import static starter.kit.model.config.Constant.TIMEOUT_READ;
import static starter.kit.model.config.Constant.TIMEOUT_WRITE;

public final class Network {

    private String baseUrl;
    private Retrofit mRetrofit;

    private OkHttpClient client;

    // Make this class a thread safe singleton
    private static class SingletonHolder {
        private static final Network INSTANCE = new Network();
    }

    public static synchronized Network get() {
        return SingletonHolder.INSTANCE;
    }

    private Network() {
    }

    public Retrofit retrofit() {
        Preconditions.checkNotNull(baseUrl, "Base URL required.");
        if (mRetrofit == null) {
            mRetrofit = newRetrofitBuilder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                    // .addConverterFactory(new StringConverterFactory())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    protected Retrofit.Builder newRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    public static class Builder {
        private String baseUrl;
        private String accept;
        private Headers.Builder headerBuilder;
        private OkHttpClient mClient;
        private boolean networkDebug;

        public Network build() {
            Preconditions.checkNotNull(baseUrl, "Base URL required.");
            ensureSaneDefaults();

            Network network = get();
            network.baseUrl = baseUrl;
            network.client = mClient;

            return network;
        }

        private void ensureSaneDefaults() {
            if (mClient == null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();

                if (headerBuilder == null) {
                    headerBuilder = defaultHeader();
                }
                if (!Strings.isBlank(accept)) {
                    headerBuilder.add("Accept", accept);
                }

                DefaultHeaderInterceptor headerInterceptor = new DefaultHeaderInterceptor(headerBuilder);
                builder.addInterceptor(headerInterceptor);
                builder.addInterceptor(new SignHeaderInterceptor());
                // 异常拦截 and token 过期重新获取
                builder.addInterceptor(new TokenHandlerInterceptor());

                // 超时设置
                builder.readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                        .writeTimeout(TIMEOUT_WRITE,TimeUnit.SECONDS)
                        .connectTimeout(TIMEOUT_CONNECTION, TimeUnit.SECONDS);

                // 日志打印
                if (networkDebug) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor((message) -> Logger.d(message));
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    builder.addInterceptor(loggingInterceptor);
                }
                mClient = builder.build();
            }
        }

        public Builder client(OkHttpClient client) {
            mClient = client;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            Preconditions.checkNotNull(baseUrl, "baseUrl == null");
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder networkDebug(boolean networkDebug) {
            this.networkDebug = networkDebug;
            return this;
        }

        public Builder accept(String accept) {
            Preconditions.checkNotNull(accept, "accept == null");
            this.accept = accept;
            return this;
        }

        public Builder addHeader(String name, String value) {
            if (headerBuilder == null) {
                headerBuilder = defaultHeader();
            }
            headerBuilder.set(name, value);
            return this;
        }

        private Headers.Builder defaultHeader() {
            final AppInfo appInfo = SupportApp.appInfo();
            Headers.Builder builder = new Headers.Builder();
            builder.add("Content-Encoding", "gzip")
                    .add("X-Client-Build", String.valueOf(appInfo.versionCode))
                    .add("X-Client-Version", appInfo.version)
                    .add("X-Client", appInfo.deviceId)
                    .add("X-Language-Code", appInfo.languageCode)
                    .add("X-Client-Type", "android");

            final String channel = appInfo.channel;
            if (!TextUtils.isEmpty(channel)) {
                builder.add("X-Client-Channel", channel);
            }
            return builder;
        }
    }
}
