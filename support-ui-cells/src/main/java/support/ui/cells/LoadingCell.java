package support.ui.cells;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import support.ui.utilities.AndroidUtilities;
import support.ui.utilities.LayoutHelper;


public class LoadingCell extends FrameLayout {

  public LoadingCell(Context context) {
    super(context);

    ProgressBar progressBar = new ProgressBar(context);
    addView(progressBar, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER));
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
        AndroidUtilities.dp(54), MeasureSpec.EXACTLY));
  }
}
