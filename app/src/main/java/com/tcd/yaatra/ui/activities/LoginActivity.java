package com.tcd.yaatra.ui.activities;

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

        layoutDataBinding.login.setOnClickListener(view -> loginActivityViewModel.authenticateUser(
                layoutDataBinding.username.getText().toString(),
                layoutDataBinding.password.getText().toString()
        ).observe(this, loginResponse -> {
            Toast.makeText(this, "Response "+ loginResponse.toString(), Toast.LENGTH_SHORT).show();
        }));
    }


}
