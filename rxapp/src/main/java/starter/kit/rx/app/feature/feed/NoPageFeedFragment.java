package starter.kit.rx.app.feature.feed;

import android.graphics.Color;
import android.os.Bundle;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import nucleus5.factory.RequiresPresenter;
import starter.kit.app.StarterFragConfig;
import starter.kit.app.StarterRecyclerFragment;
import starter.kit.rx.app.R;
import starter.kit.rx.app.model.entity.xy.News;

@RequiresPresenter(NewsPresenter.class)
public class NoPageFeedFragment extends StarterRecyclerFragment<News, NewsPresenter> {

  public static NoPageFeedFragment create() {
    NoPageFeedFragment feedFragment = new NoPageFeedFragment();
    return feedFragment;
  }

  @Override public void onCreate(Bundle bundle) {
    super.onCreate(bundle);

    StarterFragConfig.Builder builder = new StarterFragConfig.Builder()
        .addLoadingListItem(true) // 是否加载更多
        .pageSize(5)
        .bind(News.class, FeedsViewHolder.class)
        .recyclerViewDecor(new HorizontalDividerItemDecoration
            .Builder(getContext()).size(10)
            .colorResId(R.color.dividerColor)
            .build())
        .swipeRefreshLayoutColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);

    buildFragConfig(builder.build());
  }
}