package starter.kit.rx.app.feature.auth;

import android.os.Bundle;
import icepick.State;
import io.reactivex.Observable;
import starter.kit.app.NetworkPresenter;
import starter.kit.rx.app.model.entity.User;
import starter.kit.rx.app.network.ApiService;
import starter.kit.rx.app.network.service.AuthService;
import starter.kit.util.RxUtils;

/**
 * Created by YuGang Yang on 06 29, 2016.
 * Copyright 2015-2016 qiji.tech. All rights reserved.
 */
public class AuthPresenter extends NetworkPresenter<User, LoginActivity> {

  private AuthService mAuthService;

  //存储在 Icepick
  @State String username;
  @State String password;

  @Override protected void onCreate(Bundle savedState) {
    super.onCreate(savedState);
    mAuthService = ApiService.createAuthService();
  }

  @Override public Observable<User> request() {
    return mAuthService.login(username, password,"2","android");
  }

  @Override public void showHud() {
    RxUtils.showHud(getView(), "Login...", () -> stop());
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    mAuthService = null;
  }

  void requestItem(String username, String password) {
    this.username = username;
    this.password = password;
    start();
  }
}
