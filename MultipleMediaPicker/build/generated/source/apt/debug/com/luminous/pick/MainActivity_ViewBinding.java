// Generated code from Butter Knife. Do not modify!
package com.luminous.pick;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    this.target = target;

    target.imgSinglePick = Utils.findRequiredViewAsType(source, R.id.imgSinglePick, "field 'imgSinglePick'", ImageView.class);
    target.btnGalleryPick = Utils.findRequiredViewAsType(source, R.id.btnGalleryPick, "field 'btnGalleryPick'", Button.class);
    target.btnGalleryPickMul = Utils.findRequiredViewAsType(source, R.id.btnGalleryPickMul, "field 'btnGalleryPickMul'", Button.class);
    target.viewSwitcher = Utils.findRequiredViewAsType(source, R.id.viewSwitcher, "field 'viewSwitcher'", ViewSwitcher.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.recyclerView, "field 'recyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.imgSinglePick = null;
    target.btnGalleryPick = null;
    target.btnGalleryPickMul = null;
    target.viewSwitcher = null;
    target.recyclerView = null;
  }
}
