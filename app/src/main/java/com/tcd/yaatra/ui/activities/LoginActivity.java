package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityLoginBinding;
import com.tcd.yaatra.ui.viewmodels.LoginActivityViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Inject
    LoginActivityViewModel loginActivityViewModel;
    SharedPreferences loginPreferences;


    @Override
    int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.login.setOnClickListener(view -> handleOnLoginButtonClick());
        layoutDataBinding.showHide.setOnClickListener(view -> handleOnShowHideClick());
        layoutDataBinding.registerLink.setOnClickListener(view -> handleOnRegisterLinkClick());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.loginPreferences = SharedPreferenceUtils.createLoginSharedPreference();
    }

    private void handleOnLoginButtonClick() {

        if(TextUtils.isEmpty(layoutDataBinding.username.getText().toString()))
        {
            layoutDataBinding.username.setError("Username is required");
            layoutDataBinding.username.setHint("Please enter username");
            layoutDataBinding.username.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.password.getText().toString()))
        {
            layoutDataBinding.password.setError("Password is required");
            layoutDataBinding.password.setHint("Please enter password");
            layoutDataBinding.password.requestFocus();
        }

        else {
            String username = layoutDataBinding.username.getText().toString();
            String password = layoutDataBinding.password.getText().toString();

        loginActivityViewModel.authenticateUser(username, password)
                .observe(this, loginResponse -> {
                    switch (loginResponse.getState()) {
                        case LOADING:
                            layoutDataBinding.progressBarOverlay.setVisibility(View.VISIBLE);
                            break;

                        case SUCCESS:
                            layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                            Intent myIntent = new Intent(LoginActivity.this, DailyCommuteActivity.class);
                            SharedPreferenceUtils.setAuthToken(loginResponse.getData().getAuthToken());
                            startActivity(myIntent);
                            finish();
                            break;

                        case FAILURE:
                            layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                            Toast.makeText(this, loginResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    });
        }
    }

    private void handleOnShowHideClick() {

        if( layoutDataBinding.password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
            layoutDataBinding.showHide.setImageResource(R.drawable.ic_hide_password);

            //Show Password
            layoutDataBinding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            layoutDataBinding.password.setSelection(layoutDataBinding.password.length());

        }
        else{
            layoutDataBinding.showHide.setImageResource(R.drawable.ic_show_password);

            //Hide Password
            layoutDataBinding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            layoutDataBinding.password.setSelection(layoutDataBinding.password.length());

        }
    }

    private void handleOnRegisterLinkClick() {
        Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

}
