package starter.kit.retrofit;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import starter.kit.account.AccountManager;
import starter.kit.model.config.Constant;
import support.ui.utilities.Md5Util;
import support.ui.utilities.Strings;

/**
 * Created by renwoxing on 2017/9/28.
 */

public class SignHeaderInterceptor implements HeaderInterceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(setRequestHeadSign(chain.request()));
    }

    /**
     * 签名头处理
     * @param originalRequest
     * @return
     */
    private Request setRequestHeadSign(Request originalRequest) {
        Map<String, String> argMap = parseParams(originalRequest);
        if (argMap == null) {
            argMap = new HashMap<>();
        }

        final String token = AccountManager.INSTANCE.token();
        //new header sign 新增header
        String nostr = Strings.randomString(5);
        String signStr = sign(argMap, nostr);
        Request.Builder requestBuilder = originalRequest.newBuilder()
                .addHeader(Constant.APIConfig.X_JXC_APPID, Constant.APIConfig.APP_ID)
                .addHeader(Constant.APIConfig.X_JXC_NOSTR, nostr)
                .addHeader(Constant.APIConfig.X_JXC_SIGN, signStr);
        if (!Strings.isBlank(token)) {
            requestBuilder.addHeader(Constant.APIConfig.X_JXC_TOKEN, token);
        }
        return requestBuilder.build();
    }


    /**
     * 签名算法
     *
     * @param argMap
     * @param nostr
     * @return
     */
    private String sign(Map<String, String> argMap, String nostr) {
        return sign(Constant.APIConfig.APP_SECRET, nostr, argMap);
    }

    /**
     * 应用接入时，为每个应用分配一个APPID和相应的APPSecrect,在进行api请求的需要在http header（X-JXC-SIGN）中带上对参数的签名结果。
     * 签名结果=md5(s+AppSecrect+nostr)
     * 其中:
     * nostr:为随机字符串（推荐为8位）
     * s:请求的业务参数（图片、语音等排除的参数除外）按参数名字典序排序后的参数值连接结成的字符串
     */
    private String sign(String appSecret, String nostr, Map<String, String> argMap) {
        if (argMap == null)
            argMap = new HashMap<>();
        String s = sortMapAndValueToStr(argMap);
        String sign = Md5Util.md5(s + appSecret + nostr);
        Log.d("sign", "sign ---- s:" + s + "  sign:" + sign);
        return sign;
    }


    private String sortMapAndValueToStr(Map<String, String> argMap) {
        //参数值按其参数名的典序拼接+appSecret,用MD5加密
        List<String> keys = new ArrayList<>();
        for (String str : argMap.keySet()) {
            keys.add(str);
        }
        Collections.sort(keys);
        StringBuffer sb = new StringBuffer();
        for (String key : keys) {
            sb.append(argMap.get(key));
        }
        return sb.toString();
    }

    /**
     * 解析请求参数
     *
     * @param request
     * @return
     */
    public static Map<String, String> parseParams(Request request) {
        //GET POST DELETE PUT PATCH
        String method = request.method();
        Map<String, String> params = null;
        if ("GET".equalsIgnoreCase(method)) {
            params = doGet(request);
        } else if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
            RequestBody body = request.body();
            if (body != null && body instanceof FormBody) {
                params = doForm(request);
            }
        }
        return params;
    }

    /**
     * 获取get方式的请求参数
     *
     * @param request
     * @return
     */
    private static Map<String, String> doGet(Request request) {
        Map<String, String> params = null;
        HttpUrl url = request.url();
        Set<String> strings = url.queryParameterNames();
        if (strings != null) {
            Iterator<String> iterator = strings.iterator();
            params = new HashMap<>();
            int i = 0;
            while (iterator.hasNext()) {
                String name = iterator.next();
                String value = url.queryParameterValue(i);
                params.put(name, value);
                Log.d("sign", " ------ old  get name : " + name + " value:" + value);
                i++;
            }
        }
        return params;
    }

    /**
     * 获取表单的请求参数
     *
     * @param request
     * @return
     */
    private static Map<String, String> doForm(Request request) {
        Map<String, String> params = null;
        FormBody body = null;
        try {
            body = (FormBody) request.body();
        } catch (ClassCastException c) {
        }
        if (body != null) {
            int size = body.size();
            if (size > 0) {
                params = new HashMap<>();
                for (int i = 0; i < size; i++) {
                    Log.d("sign", " ------old  body form name : " + body.name(i) + " value:" + body.value(i));
                    params.put(body.name(i), body.value(i));
                }
            }
        }
        return params;
    }
}
