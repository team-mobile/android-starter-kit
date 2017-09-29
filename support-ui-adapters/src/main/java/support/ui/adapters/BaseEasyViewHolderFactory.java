package support.ui.adapters;

import android.content.Context;
import android.view.ViewGroup;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 创建 viewHolder的工厂类
 */
public class BaseEasyViewHolderFactory {

  protected Context context;

  // 存储的是 value 和 viewHolder 的对应关系
  private Map<Class, Class<? extends EasyViewHolder>> boundViewHolders = new HashMap<>();
  private List<Class> valueClassTypes = new ArrayList<>();

  public BaseEasyViewHolderFactory(Context context) {
    this.context = context;
  }

  /**
   * 根据传入的 viewType, 找到对应的 viewHolder,
   * 然后使用反射构造出 viewHolder.
   *
   * 注意:
   * 反射用的构造方法的参数是 Context, ViewGroup.
   * 所以自己写的 easyViewHolder 一定要有这个构造方法.
   * 总的来看, 这个 ViewHolderFactory 通过保存 value 和 viewHolder 的对应关系,
   * 使用反射构造出正确的 viewHolder. 这样可以方便地实现多种 viewType.
   * @param viewType
   * @param parent
   * @return
   */
  public EasyViewHolder create(int viewType, ViewGroup parent) {
    Class valueClass = valueClassTypes.get(viewType);
    try {
      Class<? extends EasyViewHolder> easyViewHolderClass = boundViewHolders.get(valueClass);
      Constructor<? extends EasyViewHolder> constructor =
          easyViewHolderClass.getDeclaredConstructor(Context.class, ViewGroup.class);
      return constructor.newInstance(context, parent);
    } catch (Throwable e) {
      throw new RuntimeException(
          "Unable to create ViewHolder for" + valueClass + ". " + e.getCause().getMessage(), e);
    }
  }

  /**
   * 把对应关系保存起来
   * @param valueClass
   * @param viewHolder
   */
  void bind(Class valueClass, Class<? extends EasyViewHolder> viewHolder) {
    valueClassTypes.add(valueClass);
    boundViewHolders.put(valueClass, viewHolder);
  }

  /**
   * 传入任意一个对象,
   * 通过这个对象所属的类, 找到对应类的 index 序号
   * @param object
   * @return
   */
  public int itemViewType(Object object) {
    return valueClassTypes.indexOf(object.getClass());
  }

  public List<Class> getValueClassTypes() {
    return valueClassTypes;
  }

  public Map<Class, Class<? extends EasyViewHolder>> getBoundViewHolders() {
    return boundViewHolders;
  }
}
