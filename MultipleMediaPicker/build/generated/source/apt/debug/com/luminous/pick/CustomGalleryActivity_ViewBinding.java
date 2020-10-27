// Generated code from Butter Knife. Do not modify!
package com.luminous.pick;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class CustomGalleryActivity_ViewBinding implements Unbinder {
  private CustomGalleryActivity target;

  @UiThread
  public CustomGalleryActivity_ViewBinding(CustomGalleryActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public CustomGalleryActivity_ViewBinding(CustomGalleryActivity target, View source) {
    this.target = target;

    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.recyclerView, "field 'recyclerView'", RecyclerView.class);
    target.imgNoMedia = Utils.findRequiredViewAsType(source, R.id.imgNoMedia, "field 'imgNoMedia'", ImageView.class);
    target.btnGalleryOk = Utils.findRequiredViewAsType(source, R.id.btnGalleryOk, "field 'btnGalleryOk'", Button.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    CustomGalleryActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerView = null;
    target.imgNoMedia = null;
    target.btnGalleryOk = null;
  }
}
