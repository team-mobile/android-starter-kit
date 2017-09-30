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

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * JacksonJsonConverterFactory
 * 自定义 json 转换工厂
 * <p>
 * Created by renwoxing on 2017/9/29.
 */
@Deprecated
public class JacksonJsonConverterFactory extends Converter.Factory {

    /**
     * Create an instance using a default {@link ObjectMapper} instance for conversion.
     */
    public static JacksonJsonConverterFactory create() {
        return create(new ObjectMapper());
    }

    /**
     * Create an instance using {@code mapper} for conversion.
     */
    @SuppressWarnings("ConstantConditions") // Guarding public API nullability.
    public static JacksonJsonConverterFactory create(ObjectMapper mapper) {
        if (mapper == null) throw new NullPointerException("mapper == null");
        return new JacksonJsonConverterFactory(mapper);
    }

    private final ObjectMapper mapper;

    private JacksonJsonConverterFactory(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type,
                                                            Annotation[] annotations,
                                                            Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectReader reader = mapper.readerFor(javaType);
        return new JacksonJsonResponseBodyConverter<>(reader);
        // Converter<ResponseBody, ?> ret =  new JacksonJsonResponseBodyConverter<>(reader);
        // return ret;

    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
                                                          Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations,
                                                          Retrofit retrofit) {
        JavaType javaType = mapper.getTypeFactory().constructType(type);
        ObjectWriter writer = mapper.writerFor(javaType);
        return new JacksonJsonRequestBodyConverter<>(writer);
    }


}
