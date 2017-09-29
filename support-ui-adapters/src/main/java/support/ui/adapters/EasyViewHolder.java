package support.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * ViewHolder 的构造方法是传入一个 View,　
 * 而 EasyViewHolder 的构造方法传入了 Context context, ViewGroup parent, int layoutId，
 * 内部会调用 LayoutInflater,　就是说简化了构造．
 * 注意:构造方法并没有使用 ButterKnife,
 * 所以继承的时候要自己写 ButterKnife.bind(this, itemView).
 * 这个 context 就是构造 easyRecyclerAdapter 时传入的context
 * 构造方法还会为 itemView 设置 OnClickListener 和 OnLongClickListener.
 * @param <V>
 */
public abstract class EasyViewHolder<V> extends RecyclerView.ViewHolder
    implements View.OnLongClickListener, View.OnClickListener {

  private OnItemClickListener itemClickListener;
  private OnItemLongClickListener longClickListener;

  public EasyViewHolder(Context context, ViewGroup parent, int layoutId) {
    this(LayoutInflater.from(context).inflate(layoutId, parent, false));
    bindListeners();
  }

  public EasyViewHolder(View itemView) {
    super(itemView);
    bindListeners();
  }

  void setItemClickListener(OnItemClickListener itemClickListener) {
    this.itemClickListener = itemClickListener;
  }

  void setLongClickListener(OnItemLongClickListener longClickListener) {
    this.longClickListener = longClickListener;
  }

  protected void bindListeners() {
    itemView.setOnClickListener(this);
    itemView.setOnLongClickListener(this);
  }

  protected void unbindListeners() {
    itemView.setOnClickListener(null);
    itemView.setOnLongClickListener(null);
  }

  @Override public boolean onLongClick(View v) {
    return longClickListener != null && longClickListener.onLongItemClicked(getAdapterPosition(),
        itemView);
  }

  /**
   * OnItemClickListener 接口里的 position 参数,
   * 就是点击的 viewHolder 调用 getAdapterPosition 得到的.
   * {@link android.support.v7.widget.RecyclerView#getAdapter#ViewHolder#getAdapterPosition()}
   * 想处理 viewHolder 的点击事件, 只需实现那个接口 OnItemClickListener.
   * @param v
   */
  @Override public void onClick(View v) {
    if (itemClickListener == null) return;
    //itemClickListener, 接口的实例
    itemClickListener.onItemClick(getAdapterPosition(), v);
  }

  /**
   * 抽象方法会在 viewHolder 绑定数据的时候调用.
   * <code>
   * @Override public void bindTo(int position, News news) {
   *   mAvatarView.setImageURI(news.thumbnail);
   *   mUsernameTextView.setText(position + "->" + news.title);
   *   mContentTextView.setText(news.description);
   * }
   * <code/>
   * @param position
   * @param value
   */
  public abstract void bindTo(int position, V value);

  public interface OnItemClickListener {
    void onItemClick(final int position, View view);
  }

  public interface OnItemLongClickListener {
    boolean onLongItemClicked(final int position, View view);
  }
}
