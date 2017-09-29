package support.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import support.ui.adapters.debounced.DebouncedOnClickListener;
import support.ui.adapters.debounced.DebouncedOnLongClickListener;

import static support.ui.adapters.EasyViewHolder.OnItemClickListener;
import static support.ui.adapters.EasyViewHolder.OnItemLongClickListener;

/**
 * 对一些常用操作进行了封装, 方便使用.
 */
public class EasyRecyclerAdapter extends RecyclerView.Adapter<EasyViewHolder> {

  //Object说明可以放任何数据
  private List<Object> dataList = new ArrayList<>();
  private BaseEasyViewHolderFactory viewHolderFactory;
  private OnItemClickListener itemClickListener;
  private OnItemLongClickListener longClickListener;

  /**
   * 这个构造方法会 构造出 ViewHolderFactory,
   * bind 方法其实就是调用 {@link BaseEasyViewHolderFactory#bind(Class, Class)} 方法.
   * @param context
   * @param valueClass
   * @param easyViewHolderClass
   */
  public EasyRecyclerAdapter(Context context, Class valueClass,
      Class<? extends EasyViewHolder> easyViewHolderClass) {
    this(context);
    bind(valueClass, easyViewHolderClass);
  }

  public EasyRecyclerAdapter(Context context) {
    this(new BaseEasyViewHolderFactory(context));
  }

  public EasyRecyclerAdapter(BaseEasyViewHolderFactory easyViewHolderFactory, Class valueClass,
      Class<? extends EasyViewHolder> easyViewHolderClass) {
    this(easyViewHolderFactory);
    bind(valueClass, easyViewHolderClass);
  }

  public void viewHolderFactory(BaseEasyViewHolderFactory easyViewHolderFactory) {
    this.viewHolderFactory = easyViewHolderFactory;
  }

  public EasyRecyclerAdapter(BaseEasyViewHolderFactory easyViewHolderFactory) {
    this.viewHolderFactory = easyViewHolderFactory;
  }

  public void bind(Class valueClass, Class<? extends EasyViewHolder> viewHolder) {
    viewHolderFactory.bind(valueClass, viewHolder);
  }

  /**
   *
   * @param parent
   * @param viewType
   * @return
   */
  @Override public EasyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    EasyViewHolder easyViewHolder = viewHolderFactory.create(viewType, parent);
    bindListeners(easyViewHolder);
    return easyViewHolder;
  }

  /**
   * 把 EasyViewHolder 的点击事件接口的实例发送给 viewHolder.
   * @param easyViewHolder
   */
  protected void bindListeners(EasyViewHolder easyViewHolder) {
    if (easyViewHolder != null) {
      easyViewHolder.setItemClickListener(itemClickListener);
      easyViewHolder.setLongClickListener(longClickListener);
    }
  }

  /**
   * 调用 easyViewHolder 的 bindTo方法.
   * {@link EasyViewHolder#bindTo(int, Object)}
   * @param holder
   * @param position
   */
  @SuppressWarnings("unchecked") @Override public void onBindViewHolder(EasyViewHolder holder, int position) {
    holder.bindTo(position, dataList.get(position));
  }

  @Override public int getItemViewType(int position) {
    return viewHolderFactory.itemViewType(dataList.get(position));
  }

  @Override public int getItemCount() {
    return dataList.size();
  }

  /**
   * 提供了新增数据方法,
   * 修改 dataList, 然后调用 notifyItemInserted 方法.
   * @see android.support.v7.widget.RecyclerView#getAdapter#ViewHolder#notifyItemInserted(int)
   * @param object
   * @param position
   */
  public void add(Object object, int position) {
    dataList.add(position, object);
    notifyItemInserted(position);
  }

  /**
   * 修改 dataList, 然后调用 notifyItemInserted 方法.
   * @see android.support.v7.widget.RecyclerView#getAdapter#ViewHolder#notifyItemInserted(int)
   * @param object
   */
  public void add(Object object) {
    dataList.add(object);
    notifyItemInserted(getIndex(object));
  }

  /**
   * 修改 dataList, 然后调用 notifyDataSetChanged 方法.
   * @see android.support.v7.widget.RecyclerView#getAdapter#ViewHolder#notifyDataSetChanged()
   * @param objects
   */
  public void addAll(List<?> objects) {
    dataList.clear();
    dataList.addAll(objects);
    notifyDataSetChanged();
  }

  /**
   * 修改 dataList, 然后调用 notifyItemRangeInserted 方法.
   * @see android.support.v7.widget.RecyclerView#getAdapter#ViewHolder#notifyItemRangeChanged(int, int)
   * @param objects
   */
  public void appendAll(List<?> objects) {
    if (objects == null) {
      throw new IllegalArgumentException("objects can not be null");
    }
    List<?> toAppends = filter(objects);
    final int toAppendSize = toAppends.size();
    if (toAppendSize <= 0) {
      return;
    }
    int prevSize = this.dataList.size();
    List<Object> data = new ArrayList<>(prevSize + toAppendSize);
    data.addAll(dataList);
    data.addAll(toAppends);
    dataList = data;
    notifyItemRangeInserted(prevSize, toAppends.size());
  }

  /**
   * 去掉重复数据
   */
  private List<?> filter(List<?> data) {
    List<Object> returnData = new ArrayList<>();
    List<?> localDataList = this.dataList;
    for (Object o : data) {
      if (!localDataList.contains(o)) {
        returnData.add(o);
      }
    }
    return returnData;
  }

  public boolean update(Object data, int position) {
    Object oldData = dataList.set(position, data);
    if (oldData != null) {
      notifyItemChanged(position);
    }
    return oldData != null;
  }

  public boolean remove(Object data) {
    return dataList.contains(data) && remove(getIndex(data));
  }

  /**
   * 修改 dataList, 然后调用 notifyItemRemoved 方法.
   * @see android.support.v7.widget.RecyclerView#getAdapter#ViewHolder#notifyItemRemoved(int)
   * @param position
   * @return
   */
  public boolean remove(int position) {
    boolean validIndex = isValidIndex(position);
    if (validIndex) {
      dataList.remove(position);
      notifyItemRemoved(position);
    }
    return validIndex;
  }

  /**
   * 修改 dataList, 然后调用 notifyDataSetChanged 方法.
   * @see android.support.v7.widget.RecyclerView#getAdapter#ViewHolder#notifyDataSetChanged
   */
  public void clear() {
    dataList.clear();
    notifyDataSetChanged();
  }

  public List<Object> getItems() {
    return dataList;
  }

  public Object get(int position) {
    return dataList.get(position);
  }

  public int getIndex(Object item) {
    return dataList.indexOf(item);
  }

  public boolean isEmpty() {
    return getItemCount() == 0;
  }

  public void setOnClickListener(final OnItemClickListener listener) {
    this.itemClickListener = new DebouncedOnClickListener() {
      @Override public boolean onDebouncedClick(View v, int position) {
        if (listener != null) {
          listener.onItemClick(position, v);
        }
        return true;
      }
    };
  }

  public void setOnLongClickListener(final OnItemLongClickListener listener) {
    this.longClickListener = new DebouncedOnLongClickListener() {
      @Override public boolean onDebouncedClick(View v, int position) {
        return listener != null && listener.onLongItemClicked(position, v);
      }
    };
  }

  private boolean isValidIndex(int position) {
    return position >= 0 && position < getItemCount();
  }
}
