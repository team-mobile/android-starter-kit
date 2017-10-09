package starter.kit.model.config;

/**
 * Created by renwoxing on 2017/9/26.
 */

public interface Constant {

    String BASE_URL_HEADER_KEY = "base_url";
    int TIMEOUT_WRITE = 10;
    int TIMEOUT_READ = 10;
    int TIMEOUT_CONNECTION = 15;

    int TOKEN_EXPIRE_ERROR_CODE = 40102;


    class APIConfig {

        public static final String X_JXC_APPID = "X-JXC-APPID";
        public static final String X_JXC_NOSTR = "X-JXC-NOSTR";
        public static final String X_JXC_SIGN = "X-JXC-SIGN";
        public static final String X_JXC_TOKEN = "X-JXC-TOKEN";


        public final static String APP_ID = "APP_ID";
        public final static String APP_SECRET = "APP_SECRET";


        public static final String KEY_TOKEN = "key_token";
        public static final String KEY_REFRESH_TOKEN = "key_refresh_token";
        public static final String KEY_EXPIRES = "key_expires"; //单位：秒
        public static final String KEY_SAVE_TIME = "key_save_time"; //保存
    }
}
