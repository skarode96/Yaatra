package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityLoginBinding;
import com.tcd.yaatra.ui.viewmodels.LoginActivityViewModel;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Inject
    LoginActivityViewModel loginActivityViewModel;

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();

        // Login Button onClick
        layoutDataBinding.login.setOnClickListener(view -> handleOnLoginButtonClick());
    }

    private void handleOnLoginButtonClick() {


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
                            myIntent.putExtra("token", loginResponse.getData().getToken());
                            startActivity(myIntent);
                            break;

                        case FAILURE:
                            layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                            Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                            break;
                    }
                });
    }

}
