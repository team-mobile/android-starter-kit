package support.ui.cells;

import android.view.View;
import support.ui.adapters.EasyViewHolder;


public class CellViewHolder extends EasyViewHolder<CellModel> implements View.OnClickListener {

  public CellViewHolder(View itemView) {
    super(itemView);
    setBackground();
  }

  @Override public void bindTo(int position, CellModel value) {
    if (value.enabled) {
      bindListeners();
    } else {
      unbindListeners();
    }
  }

  private void setBackground() {
    if (itemView.getBackground() == null) {
      itemView.setBackgroundResource(R.drawable.list_selector_white);
    }
  }
}
