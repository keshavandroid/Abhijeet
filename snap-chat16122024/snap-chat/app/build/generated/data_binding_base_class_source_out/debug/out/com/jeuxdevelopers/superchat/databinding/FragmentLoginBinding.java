// Generated by view binder compiler. Do not edit!
package com.jeuxdevelopers.superchat.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.jeuxdevelopers.superchat.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentLoginBinding implements ViewBinding {
  @NonNull
  private final ScrollView rootView;

  @NonNull
  public final ImageButton btnGoogleLogin;

  @NonNull
  public final MaterialButton btnLogin;

  @NonNull
  public final TextInputLayout inputEmail;

  @NonNull
  public final TextInputLayout inputPassword;

  @NonNull
  public final TextView orWith;

  @NonNull
  public final TextView tvForgotPassword;

  @NonNull
  public final TextView tvGoSignUp;

  private FragmentLoginBinding(@NonNull ScrollView rootView, @NonNull ImageButton btnGoogleLogin,
      @NonNull MaterialButton btnLogin, @NonNull TextInputLayout inputEmail,
      @NonNull TextInputLayout inputPassword, @NonNull TextView orWith,
      @NonNull TextView tvForgotPassword, @NonNull TextView tvGoSignUp) {
    this.rootView = rootView;
    this.btnGoogleLogin = btnGoogleLogin;
    this.btnLogin = btnLogin;
    this.inputEmail = inputEmail;
    this.inputPassword = inputPassword;
    this.orWith = orWith;
    this.tvForgotPassword = tvForgotPassword;
    this.tvGoSignUp = tvGoSignUp;
  }

  @Override
  @NonNull
  public ScrollView getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentLoginBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentLoginBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_login, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentLoginBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btnGoogleLogin;
      ImageButton btnGoogleLogin = ViewBindings.findChildViewById(rootView, id);
      if (btnGoogleLogin == null) {
        break missingId;
      }

      id = R.id.btnLogin;
      MaterialButton btnLogin = ViewBindings.findChildViewById(rootView, id);
      if (btnLogin == null) {
        break missingId;
      }

      id = R.id.inputEmail;
      TextInputLayout inputEmail = ViewBindings.findChildViewById(rootView, id);
      if (inputEmail == null) {
        break missingId;
      }

      id = R.id.inputPassword;
      TextInputLayout inputPassword = ViewBindings.findChildViewById(rootView, id);
      if (inputPassword == null) {
        break missingId;
      }

      id = R.id.orWith;
      TextView orWith = ViewBindings.findChildViewById(rootView, id);
      if (orWith == null) {
        break missingId;
      }

      id = R.id.tvForgotPassword;
      TextView tvForgotPassword = ViewBindings.findChildViewById(rootView, id);
      if (tvForgotPassword == null) {
        break missingId;
      }

      id = R.id.tvGoSignUp;
      TextView tvGoSignUp = ViewBindings.findChildViewById(rootView, id);
      if (tvGoSignUp == null) {
        break missingId;
      }

      return new FragmentLoginBinding((ScrollView) rootView, btnGoogleLogin, btnLogin, inputEmail,
          inputPassword, orWith, tvForgotPassword, tvGoSignUp);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}