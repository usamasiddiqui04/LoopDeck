// Generated code from Butter Knife. Do not modify!
package com.luminous.pick.Adapter;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.luminous.pick.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ImageListRecyclerAdapter$VerticalItemHolder_ViewBinding implements Unbinder {
  private ImageListRecyclerAdapter.VerticalItemHolder target;

  @UiThread
  public ImageListRecyclerAdapter$VerticalItemHolder_ViewBinding(ImageListRecyclerAdapter.VerticalItemHolder target,
      View source) {
    this.target = target;

    target.imgQueue = Utils.findRequiredViewAsType(source, R.id.imgQueue, "field 'imgQueue'", ImageView.class);
    target.imgQueueMultiSelected = Utils.findRequiredViewAsType(source, R.id.imgQueueMultiSelected, "field 'imgQueueMultiSelected'", ImageView.class);
    target.container = Utils.findRequiredView(source, R.id.container, "field 'container'");
  }

  @Override
  @CallSuper
  public void unbind() {
    ImageListRecyclerAdapter.VerticalItemHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.imgQueue = null;
    target.imgQueueMultiSelected = null;
    target.container = null;
  }
}
