package starter.kit.retrofit.error;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {

    @JsonProperty("errcode")
    public int mStatusCode;
    @JsonProperty("errmsg")
    public String mMessage;
    @JsonProperty("message")
    public String mTitle;

    public ErrorResponse() {
    }

    public ErrorResponse(int statusCode, String title, String msg) {
        this.mStatusCode = statusCode;
        this.mTitle = title;
        this.mMessage = msg;
    }

    public ErrorResponse(int statusCode, String msg) {
        this.mStatusCode = statusCode;
        this.mMessage = msg;
        this.mTitle = msg;
    }

    public String getMessage() {
        return mMessage == null ? mTitle : mMessage;
    }

    public int getStatusCode() {
        return mStatusCode;
    }
}
