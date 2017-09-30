package starter.kit.util;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import starter.kit.retrofit.error.ErrorResponse;
import starter.kit.retrofit.error.RetrofitException;

public final class ErrorHandler {

    public static ErrorResponse handleThrowable(Throwable throwable) {
        if (throwable instanceof RetrofitException) {
            // Logger.e(throwable,throwable.getMessage());
            RetrofitException retrofitException = (RetrofitException) throwable;
            ErrorResponse errorResponse = null;
            try {
                if (retrofitException.getKind().equals(RetrofitException.Kind.HTTP)){
                    errorResponse = new ErrorResponse(400, throwable.getMessage());
                }else {
                    errorResponse = retrofitException.getErrorBodyAs(ErrorResponse.class);
                }
            } catch (IOException e) {
                // e.printStackTrace();
            } catch (RuntimeException e) {
            } catch (Exception e) {
            } finally {
                if (null != errorResponse && errorResponse.getStatusCode() != 0 && !TextUtils.isEmpty(errorResponse.getMessage())) {
                    return errorResponse;
                } else {
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        errorResponse = mapper.readValue(throwable.getMessage(), ErrorResponse.class);
                        return errorResponse;
                    } catch (IOException e1) {
                        return new ErrorResponse(500, e1.getLocalizedMessage());
                    }
                }
            }
        }
        return null;
    }
}
