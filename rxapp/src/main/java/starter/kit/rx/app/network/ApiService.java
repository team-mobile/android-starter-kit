package starter.kit.rx.app.network;

import retrofit2.Retrofit;
import starter.kit.retrofit.Network;
import starter.kit.rx.app.network.service.AuthService;
import starter.kit.rx.app.network.service.FeedService;
import starter.kit.rx.app.network.service.NewsService;

public class ApiService {

  public static AuthService createAuthService() {
    return retrofit().create(AuthService.class);
  }

  public static FeedService createFeedService() {
    return retrofit().create(FeedService.class);
  }

  private static Retrofit retrofit() {
    return Network.get().retrofit();
  }

//  private static Retrofit retJxRofit() {
//    return new Network.Builder()
//            .networkDebug(true)
//            .accept(Profile.JX_API_ACCEPT)
//            .baseUrl(JX_API_BASE_URL)
//            .build()
//            .retrofit();
//  }

  public static NewsService createXYNewsService() {
    return retrofit().create(NewsService.class);
  }
}
