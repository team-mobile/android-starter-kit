package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import nucleus5.factory.RequiresPresenter;
import starter.kit.app.StarterFragConfig;
import starter.kit.app.StarterRecyclerFragment;
import starter.kit.rx.app.R;
import starter.kit.rx.app.model.entity.Feed;

@RequiresPresenter(IdFeedPresenter.class)
public class IdFeedFragment extends StarterRecyclerFragment<Feed, IdFeedPresenter> {

  public static IdFeedFragment create() {
    return new IdFeedFragment();
  }

  /**
   * 开发者在这里应该调用 buildFragConfig 方法,
   * 参数为 StarterFragConfig, 建议通过 builder 构造.
   * buildFragConfig 方法会配置 recyclerViewAdapter 的 viewHolderFactory 和 bind,
   * 然后把 StarterFragConfig 保存起来.
   * @see starter.kit.app.StarterFragConfig.Builder
   * #
   * @param bundle
   */
  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    StarterFragConfig.Builder builder = new StarterFragConfig.Builder()
        .pageSize(5)
        .bind(Feed.class, FeedsViewHolder.class)
        .withKeyRequest(true)
        .loadingTriggerThreshold(0)
        .recyclerViewDecor(new HorizontalDividerItemDecoration
            .Builder(getContext()).size(10)
            .colorResId(R.color.dividerColor)
            .build())
        .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);

    buildFragConfig(builder.build());
  }
}