package starter.kit.rx.app.feature.auth;

import android.os.Bundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;

import java.util.List;

import icepick.State;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import starter.kit.account.AccountManager;
import starter.kit.app.NetworkPresenter;
import starter.kit.model.entity.Account;
import starter.kit.model.entity.TokenEntity;
import starter.kit.retrofit.error.RetrofitException;
import starter.kit.rx.app.model.entity.cloudweb.Organization;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.AuthService;
import starter.kit.rx.app.network.service.CloudWebService;
import starter.kit.util.RxUtils;

import static starter.kit.rx.app.model.config.Constant.JX_API_PLATFORM_ANDROID;
import static starter.kit.rx.app.model.config.Constant.JX_AUTH_API_ROLE_TEACHER;


public class AuthPresenter extends NetworkPresenter<List<Organization>, LoginActivity> {

    private AuthService mAuthService;
    private CloudWebService mCloudWebService;

    //存储在 Icepick
    @State
    String username;
    @State
    String password;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        mAuthService = ApiService.createAuthService();
        mCloudWebService = ApiService.createCloudWebService();
    }

    @Override
    public Observable<List<Organization>> request() {
        // return mAuthService.login(username, password, JX_AUTH_API_ROLE_TEACHER, JX_API_PLATFORM_ANDROID);
        return mAuthService.login(username, password, JX_AUTH_API_ROLE_TEACHER, JX_API_PLATFORM_ANDROID)
                .flatMap(new Function<TokenEntity, ObservableSource<List<Organization>>>() {
                    @Override
                    public ObservableSource<List<Organization>> apply(@NonNull TokenEntity tokenEntity) throws Exception {
                        if (null != tokenEntity) {
                            AccountManager.INSTANCE.storeAccount(new Account() {
                                @Override
                                public String name() {
                                    return username;
                                }
                                @Override
                                public String token() {
                                    return tokenEntity.accessToken;
                                }

                                @Override
                                public String toJson() {
                                    try {
                                        ObjectMapper mapper = new ObjectMapper();
                                        return mapper.writeValueAsString(tokenEntity);
                                    } catch (Exception e) {
                                        return null;
                                    }
                                }
                            });
                        }
                        return mCloudWebService.orgList();
                    }
                });
    }

    @Override
    public void showHud() {
        RxUtils.showHud(getView(), "Login...", () -> stop());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthService = null;
    }

    void requestItem(String username, String password) {
        this.username = username;
        this.password = password;
        start();
    }

    void getTeacherSchool(){
        Logger.d("------------- getTeacherSchool ");
        mCloudWebService.orgList().subscribe(new Observer<List<Organization>>() {
            @Override
            public void onSubscribe(@NonNull Disposable disposable) {

            }

            @Override
            public void onNext(@NonNull List<Organization> organizations) {
                Logger.d(" CloudWebService.orgList 请求成功。size: %s",organizations.size());
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                if (throwable instanceof RetrofitException){
                    RetrofitException exception = (RetrofitException)throwable;
                    Logger.e(" CloudWebService.orgList 请求失败： \n %s, %s, %s ", exception.getKind(),exception.getMessage(),exception.getLocalizedMessage());
                }
                Logger.e(" CloudWebService.orgList 请求失败： \n %s ", throwable);
            }

            @Override
            public void onComplete() {
                Logger.d(" CloudWebService.orgList 请求onComplete。");
            }
        });
    }
}
