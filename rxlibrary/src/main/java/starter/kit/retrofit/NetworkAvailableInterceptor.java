package starter.kit.retrofit;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import support.ui.utilities.NetworkUtils;

/**
 * Created by renwoxing on 2017/9/27.
 */

public class NetworkAvailableInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        if (NetworkUtils.isConnected()){
            if (NetworkUtils.is4G()){
                return new Response
                        .Builder()
                        .code(505)
                        .message("正在使用移动网络！")
                        .build();
            }else {
                Log.d("NetworkAvailable","网络正常");
                return chain.proceed(chain.request());
            }
        }else{
            // throw new RetrofitException("------------没有移动网络-----------",null,null, RetrofitException.Kind.NETWORK,null,null);
            Log.d("NetworkAvailable","------ XXXXXXXXXX  network is unavailable  XXXXXXXXXXX -------- ");
            if (!NetworkUtils.isWifiAvailable()){
                return new Response
                        .Builder()
                        .code(505)
                        .message("Wifi不可用！")
                        .build();
            }
            return new Response
                    .Builder()
                    .code(505)
                    .message("没有移动网络！")
                    .build();

        }
    }
}
