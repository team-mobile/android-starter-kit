/**
 * Created by YuGang Yang on September 24, 2015.
 * Copyright 2007-2015 Laputapp.com. All rights reserved.
 */
package starter.kit.rx.app.feature.feed;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import starter.kit.rx.app.R;
import starter.kit.rx.app.model.entity.Image;
import starter.kit.rx.app.model.entity.xy.News;
import starter.kit.rx.app.util.NineGridAdapter;
import support.ui.adapters.EasyViewHolder;
import support.ui.widget.NineGirdView;

import static starter.kit.rx.app.model.config.Constant.IMAGE_DEFAULT;

public class FeedsViewHolder extends EasyViewHolder<News> {

  @BindView(R.id.image_feed_user_avatar) SimpleDraweeView mAvatarView;
  @BindView(R.id.text_feed_username) TextView mUsernameTextView;
  @BindView(R.id.text_feed_content) TextView mContentTextView;
  @BindView(R.id.feed_photo_view) NineGirdView mNineGridView;

  public FeedsViewHolder(Context context, ViewGroup parent) {
    super(context, parent, R.layout.list_item_feed);
    ButterKnife.bind(this, itemView);
    mNineGridView.setNineGridAdapter(new NineGridAdapter());
    mNineGridView.setStyle(NineGirdView.STYLE_GRID);
  }

  @Override public void bindTo(int position, News news) {
    mAvatarView.setImageURI(TextUtils.isEmpty(news.thumbnail)? IMAGE_DEFAULT :news.thumbnail);
    mUsernameTextView.setText(position + "->" + news.title);
    mContentTextView.setText(news.description);
//    ArrayList<Image> images = news.thumbnail;
//    if (images != null && images.size() > 0) {
//      ViewUtils.setGone(mNineGridView, false);
//      // mNineGridView.notifyDataSetChanged(buildImageUrls(feed.images));
//    } else {
//      ViewUtils.setGone(mNineGridView, true);
//    }
  }

  private List<Uri> buildImageUrls(ArrayList<Image> images) {
    List<Uri> imageUrls = new ArrayList<>(images.size());
    for (Image image : images) {
      imageUrls.add(Uri.parse(image.url));
    }
    return imageUrls;
  }
}
