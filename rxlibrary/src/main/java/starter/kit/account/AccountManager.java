package starter.kit.account;

import android.content.Context;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import starter.kit.model.entity.Account;
import starter.kit.app.StarterApp;
import starter.kit.model.entity.TokenEntity;

public enum AccountManager {

    INSTANCE;

    private final AuthPreferences authPreferences;
    private Account mCurrentAccount;
    private Context mContext;
    private TokenEntity mTokenEntity;

    AccountManager() {
        mContext = StarterApp.appContext();
        authPreferences = new AuthPreferences(mContext);
    }

    public boolean isLogin() {
        return authPreferences.isLogin();
    }

    public void logout() {
        mCurrentAccount = null;
        authPreferences.clear();
    }

    public void storeAccount(Account account) {
        mCurrentAccount = account;
        authPreferences.setToken(account.token());
        authPreferences.setUser(account.name());
        authPreferences.setUserData(account.toJson());
        try{
            ObjectMapper mapper = new ObjectMapper();
            mTokenEntity = mapper.readValue(account.toJson(),TokenEntity.class);
        }catch (IOException e){}
    }

    public String token() {
        return authPreferences.getToken();
    }

    public String user() {
        return authPreferences.getUser();
    }

    @SuppressWarnings("unchecked")
    public TokenEntity TokenEntity() {
        if (null != mTokenEntity){
            return mTokenEntity;
        }
        try {
            String tokenJson = authPreferences.getUserData();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(tokenJson, TokenEntity.class);
        } catch (IOException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Account> T getCurrentAccount() {
        if (mCurrentAccount == null) {
            String accountJson = authPreferences.getUserData();
            if (!TextUtils.isEmpty(accountJson) && mContext instanceof AccountProvider) {
                mCurrentAccount = ((AccountProvider) mContext).provideAccount(accountJson);
            }
        }
        return (T) mCurrentAccount;
    }
}
