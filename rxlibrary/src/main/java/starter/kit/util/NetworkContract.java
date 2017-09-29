package starter.kit.util;

/**
 * view 接口
 * ContentInterface 接口
 * HudInterface 接口
 * ProgressInterface 接口
 */
public interface NetworkContract {

  interface View<T> {
    void onSuccess(T data);

    void onError(Throwable throwable);
  }

  interface HudInterface {
    void showHud();
  }

  interface ProgressInterface {
    void showProgress();

    void hideProgress();
  }

  interface ContentInterface<T> extends ProgressInterface, View<T> {
  }
}
