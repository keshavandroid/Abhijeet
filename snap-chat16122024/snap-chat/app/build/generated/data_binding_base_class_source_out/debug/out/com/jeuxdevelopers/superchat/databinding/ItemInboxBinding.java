// Generated by view binder compiler. Do not edit!
package com.jeuxdevelopers.superchat.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.jeuxdevelopers.superchat.R;
import com.vanniktech.emoji.EmojiTextView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemInboxBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CircleImageView civProfile;

  @NonNull
  public final EmojiTextView tvMessage;

  @NonNull
  public final TextView tvMsgCount;

  @NonNull
  public final TextView tvTime;

  @NonNull
  public final TextView tvUserName;

  private ItemInboxBinding(@NonNull ConstraintLayout rootView, @NonNull CircleImageView civProfile,
      @NonNull EmojiTextView tvMessage, @NonNull TextView tvMsgCount, @NonNull TextView tvTime,
      @NonNull TextView tvUserName) {
    this.rootView = rootView;
    this.civProfile = civProfile;
    this.tvMessage = tvMessage;
    this.tvMsgCount = tvMsgCount;
    this.tvTime = tvTime;
    this.tvUserName = tvUserName;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemInboxBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemInboxBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_inbox, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemInboxBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.civ_profile;
      CircleImageView civProfile = ViewBindings.findChildViewById(rootView, id);
      if (civProfile == null) {
        break missingId;
      }

      id = R.id.tvMessage;
      EmojiTextView tvMessage = ViewBindings.findChildViewById(rootView, id);
      if (tvMessage == null) {
        break missingId;
      }

      id = R.id.tvMsgCount;
      TextView tvMsgCount = ViewBindings.findChildViewById(rootView, id);
      if (tvMsgCount == null) {
        break missingId;
      }

      id = R.id.tvTime;
      TextView tvTime = ViewBindings.findChildViewById(rootView, id);
      if (tvTime == null) {
        break missingId;
      }

      id = R.id.tv_user_name;
      TextView tvUserName = ViewBindings.findChildViewById(rootView, id);
      if (tvUserName == null) {
        break missingId;
      }

      return new ItemInboxBinding((ConstraintLayout) rootView, civProfile, tvMessage, tvMsgCount,
          tvTime, tvUserName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
