package starter.kit.rx.app.network;

import retrofit2.Retrofit;
import starter.kit.retrofit.Network;
import starter.kit.rx.app.network.service.AuthService;
import starter.kit.rx.app.network.service.CloudWebService;
import starter.kit.rx.app.network.service.FeedService;
import starter.kit.rx.app.network.service.NewsService;

public class ApiService {

  private static Retrofit retrofit() {
    return Network.get().retrofit();
  }


  public static AuthService createAuthService() {
    return retrofit().create(AuthService.class);
  }

  public static FeedService createFeedService() {
    return retrofit().create(FeedService.class);
  }

  public static NewsService createXYNewsService() {
    return retrofit().create(NewsService.class);
  }

  public static CloudWebService createCloudWebService() {
    return retrofit().create(CloudWebService.class);
  }
}
