/**
 * Copyright (c) 2016-present, rener.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package starter.kit.retrofit.converter;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.orhanobut.logger.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Converter;
import starter.kit.retrofit.error.ErrorResponse;
import starter.kit.retrofit.error.RetrofitException;
import starter.kit.retrofit.interceptor.TokenHandlerInterceptor;

/**
 * 对 Response body 进行转换。
 * 也可以使用拦截处理
 *
 * @param <T>
 * @see TokenHandlerInterceptor
 */
// @Deprecated
final public class JacksonJsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final ObjectReader adapter;

    JacksonJsonResponseBodyConverter(ObjectReader adapter) {
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            BufferedSource source = value.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = value.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    //Couldn't decode the response body; charset is likely malformed.
                    throw new IOException(e.getMessage());
                }
            }

            if (!isPlaintext(buffer)) {
                Logger.i("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                throw new IOException("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
            }

            long contentLength = value.contentLength();
            if (contentLength != 0) {
                String result = buffer.clone().readString(charset);
                ObjectMapper mapper = new ObjectMapper();
                ErrorResponse errorResponse = mapper.readValue(buffer.clone().readByteArray(), ErrorResponse.class);
                if (null != errorResponse && errorResponse.getStatusCode() != 0 && !TextUtils.isEmpty(errorResponse.getMessage())) {
                    //throw RetrofitException.httpError();
                    throw RetrofitException.unexpectedError(new Throwable(result));
                }
                // 获取到 response 的 body 的 string 字符串
                // Logger.d("ResponseBody buffer.clone().readString: \n" + result);
            }

            return adapter.readValue(buffer.clone().inputStream());
            //return adapter.readValue(value.charStream());
        } finally {
            value.close();
        }
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

//    private boolean bodyEncoded(Headers headers) {
//        String contentEncoding = headers.get("Content-Encoding");
//        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
//    }
}
