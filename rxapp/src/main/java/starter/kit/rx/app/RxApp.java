package starter.kit.rx.app;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import starter.kit.app.StarterApp;
import starter.kit.model.entity.Account;
import starter.kit.model.entity.TokenEntity;
import starter.kit.retrofit.Network;
import starter.kit.rx.app.model.config.Constant;
import starter.kit.rx.app.util.InitializeUtil;

public class RxApp extends StarterApp {

  @Override public void onCreate() {
    super.onCreate();

    new Network.Builder()
        .networkDebug(true)
        .accept(Constant.JX_API_ACCEPT)
        .baseUrl(BuildConfig.API_HOST)
        .build();

    Fresco.initialize(appContext());

    InitializeUtil.initialize();

  }

  @Override protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(this);
  }

  @Override public Account provideAccount(String accountJson) {
    // 2016/11/10 Must Convert to Account Obj
    ObjectMapper mapper = new ObjectMapper();
    try {
      TokenEntity token = mapper.readValue(accountJson,TokenEntity.class);
    } catch (IOException e) {
      //e.printStackTrace();
    }
    return null;
  }
}
