package starter.kit.retrofit.interceptor;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;
import starter.kit.retrofit.error.ErrorResponse;
import starter.kit.retrofit.error.RetrofitException;

/**
 * http 服务端异常拦截器
 * <p>
 *     可以重新获取 token
 * Created by renwoxing on 2017/9/30.
 */
public class ErrorHandlerInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        if(!HttpHeaders.hasBody(response)){
            //END HTTP
            Logger.e("END HTTP");
        } else if (bodyEncoded(response.headers())) {
            //HTTP (encoded body omitted)
            Logger.e("HTTP (encoded body omitted)");
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    //Couldn't decode the response body; charset is likely malformed.
                    return response;
                }
            }

            if (!isPlaintext(buffer)) {
                Logger.i("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                return response;
            }
            if (contentLength != 0) {
                ObjectMapper mapper = new ObjectMapper();
                ErrorResponse errorResponse = mapper.readValue(buffer.clone().readByteArray(), ErrorResponse.class);
                if (null != errorResponse && errorResponse.getStatusCode() != 0 && !TextUtils.isEmpty(errorResponse.getMessage())) {
                    String result = buffer.clone().readString(charset);
                    throw RetrofitException.unexpectedError(new Throwable(result));
                }
            }
        }
        return response;
    }

    static boolean isPlaintext(Buffer buffer) throws EOFException {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

}
