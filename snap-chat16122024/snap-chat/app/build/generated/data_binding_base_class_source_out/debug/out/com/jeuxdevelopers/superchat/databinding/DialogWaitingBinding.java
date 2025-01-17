// Generated by data binding compiler. Do not edit!
package com.jeuxdevelopers.superchat.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.jeuxdevelopers.superchat.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class DialogWaitingBinding extends ViewDataBinding {
  @NonNull
  public final ProgressBar pbWait;

  @NonNull
  public final TextView tvStatusLoading;

  protected DialogWaitingBinding(Object _bindingComponent, View _root, int _localFieldCount,
      ProgressBar pbWait, TextView tvStatusLoading) {
    super(_bindingComponent, _root, _localFieldCount);
    this.pbWait = pbWait;
    this.tvStatusLoading = tvStatusLoading;
  }

  @NonNull
  public static DialogWaitingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.dialog_waiting, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static DialogWaitingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<DialogWaitingBinding>inflateInternal(inflater, R.layout.dialog_waiting, root, attachToRoot, component);
  }

  @NonNull
  public static DialogWaitingBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.dialog_waiting, null, false, component)
   */
  @NonNull
  @Deprecated
  public static DialogWaitingBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<DialogWaitingBinding>inflateInternal(inflater, R.layout.dialog_waiting, null, false, component);
  }

  public static DialogWaitingBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static DialogWaitingBinding bind(@NonNull View view, @Nullable Object component) {
    return (DialogWaitingBinding)bind(component, view, R.layout.dialog_waiting);
  }
}
