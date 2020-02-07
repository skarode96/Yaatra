package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityLoginBinding;
import com.tcd.yaatra.databinding.ActivityRegisterBinding;
import com.tcd.yaatra.services.api.yaatra.models.RegisterRequestBody;
import com.tcd.yaatra.ui.viewmodels.LoginActivityViewModel;
import com.tcd.yaatra.ui.viewmodels.RegisterActivityViewModel;
import com.tcd.yaatra.utils.SharedPreferenceUtils;

import javax.inject.Inject;

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> {

    @Inject
    RegisterActivityViewModel registerActivityViewModel;

    @Override
    int getLayoutResourceId() {
        return R.layout.activity_register;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.btnRegister.setOnClickListener(view -> handleOnRegisterButtonClick());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void handleOnRegisterButtonClick() {

        if(TextUtils.isEmpty(layoutDataBinding.userName.getText().toString()))
        {
            layoutDataBinding.userName.setError("Username is required");
            layoutDataBinding.userName.setHint("Please enter username");
            layoutDataBinding.userName.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.firstName.getText().toString()))
        {
            layoutDataBinding.firstName.setError("First Name is required");
            layoutDataBinding.firstName.setHint("Please enter first name");
            layoutDataBinding.firstName.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.lastName.getText().toString()))
        {
            layoutDataBinding.lastName.setError("Last Name is required");
            layoutDataBinding.lastName.setHint("Please enter Last Name");
            layoutDataBinding.lastName.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.emailId.getText().toString()))
        {
            layoutDataBinding.emailId.setError("Email Id is required");
            layoutDataBinding.emailId.setHint("Please enter email id");
            layoutDataBinding.emailId.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.phoneNum.getText().toString()))
        {
            layoutDataBinding.phoneNum.setError("Phone Number is required");
            layoutDataBinding.phoneNum.setHint("Please enter phone number");
            layoutDataBinding.phoneNum.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.country.getText().toString()))
        {
            layoutDataBinding.country.setError("Country is required");
            layoutDataBinding.country.setHint("Please enter country");
            layoutDataBinding.country.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.age.getText().toString()))
        {
            layoutDataBinding.age.setError("Age is required");
            layoutDataBinding.age.setHint("Please enter age");
            layoutDataBinding.age.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.userPassword.getText().toString()))
        {
            layoutDataBinding.userPassword.setError("Password is required");
            layoutDataBinding.userPassword.setHint("Please enter password");
            layoutDataBinding.userPassword.requestFocus();
        }

        else if(TextUtils.isEmpty(layoutDataBinding.userCPassword.getText().toString()))
        {
            layoutDataBinding.userCPassword.setError("Confirmed Password is required");
            layoutDataBinding.userCPassword.setHint("Please enter confirmed password");
            layoutDataBinding.userCPassword.requestFocus();
        }


        else
        {
            RegisterRequestBody registerObject = new RegisterRequestBody();

            registerObject.setUserName(layoutDataBinding.userName.getText().toString());
            registerObject.setFirstName(layoutDataBinding.firstName.getText().toString());
            registerObject.setLastName(layoutDataBinding.lastName.getText().toString());
            registerObject.setEmail(layoutDataBinding.emailId.getText().toString());
            registerObject.setAge(Integer.parseInt(layoutDataBinding.age.getText().toString()));
            registerObject.setPassword(layoutDataBinding.userPassword.getText().toString());
            registerObject.setConfirmPassword(layoutDataBinding.userCPassword.getText().toString());

            RadioButton genderBtn = (RadioButton)findViewById(layoutDataBinding.genderGroup.getCheckedRadioButtonId());
            if(genderBtn.getText().toString().equalsIgnoreCase("Male"))
                registerObject.setGender(1);
            else if(genderBtn.getText().toString().equalsIgnoreCase("Female"))
                registerObject.setGender(2);
            else if(genderBtn.getText().toString().equalsIgnoreCase("Others"))
                registerObject.setGender(3);


            int phoneNumber = Integer.parseInt(layoutDataBinding.phoneNum.getText().toString());
            String country = layoutDataBinding.country.getText().toString();
            RadioButton genderPrefBtn = (RadioButton)findViewById(layoutDataBinding.genderPrefGroup.getCheckedRadioButtonId());
            String genderPref = genderPrefBtn.getText().toString();
            RadioButton transportPrefBtn = (RadioButton)findViewById(layoutDataBinding.transportPrefGroup.getCheckedRadioButtonId());
            String transportPref = transportPrefBtn.getText().toString();

            registerActivityViewModel.register(registerObject).observe(this,registerResponse -> {
                switch (registerResponse.getState()) {
                    case LOADING:
                        layoutDataBinding.progressBarOverlay.setVisibility(View.VISIBLE);
                        break;

                    case SUCCESS:
                        layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                        Toast.makeText(this,"User creation successful",Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(myIntent);
                        finish();
                        break;

                    case FAILURE:
                        layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                        Toast.makeText(this, registerResponse.getData().getMessage(), Toast.LENGTH_SHORT).show();
                        break;
                }
            });

        }
    }
}
