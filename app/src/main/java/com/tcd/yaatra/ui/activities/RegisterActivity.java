package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityRegisterBinding;
import com.tcd.yaatra.repository.models.Gender;
import com.tcd.yaatra.repository.models.TransportPreference;
import com.tcd.yaatra.services.api.yaatra.models.RegisterRequestBody;
import com.tcd.yaatra.ui.viewmodels.RegisterActivityViewModel;

import javax.inject.Inject;

public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> {

    @Inject
    RegisterActivityViewModel registerActivityViewModel;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initEventHandlers() {
        super.initEventHandlers();
        layoutDataBinding.btnRegister.setOnClickListener(view -> handleOnRegisterButtonClick());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    private void handleOnRegisterButtonClick() {

        Gender male = Gender.MALE;
        Gender female = Gender.FEMALE;
        Gender noPref = Gender.NOT_SPECIFIED;
        Gender other = Gender.OTHER;

        TransportPreference noTransportPref = TransportPreference.NO_PREFERENCE;
        TransportPreference walk = TransportPreference.WALK;
        TransportPreference taxi = TransportPreference.TAXI;

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
            registerObject.setConfirmPassword(layoutDataBinding.userCPassword.getText().toString());

            long phoneNumber = Long.parseLong(layoutDataBinding.phoneNum.getText().toString());
            String country = layoutDataBinding.country.getText().toString();
            registerObject.setCountry(country);
            registerObject.setPhoneNumber(phoneNumber);

            RadioButton genderBtn = (RadioButton)findViewById(layoutDataBinding.genderGroup.getCheckedRadioButtonId());
            if(genderBtn.getText().toString().equalsIgnoreCase(male.stringLabel))
                registerObject.setGender(male.idName);
            else if(genderBtn.getText().toString().equalsIgnoreCase(female.stringLabel))
                registerObject.setGender(female.idName);
            else if(genderBtn.getText().toString().equalsIgnoreCase(other.stringLabel))
                registerObject.setGender(other.idName);


            RadioButton genderPrefBtn = (RadioButton)findViewById(layoutDataBinding.genderPrefGroup.getCheckedRadioButtonId());
            if(genderPrefBtn.getText().toString().equalsIgnoreCase(male.stringLabel))
                registerObject.setPrefGender(male.idNumber);
            else if(genderPrefBtn.getText().toString().equalsIgnoreCase(female.stringLabel))
                registerObject.setPrefGender(female.idNumber);
            else if(genderPrefBtn.getText().toString().equalsIgnoreCase(other.stringLabel))
                registerObject.setPrefGender(other.idNumber);
            else if(genderPrefBtn.getText().toString().equalsIgnoreCase(noPref.stringLabel))
                registerObject.setPrefGender(noPref.idNumber);

            RadioButton transportPrefBtn = (RadioButton)findViewById(layoutDataBinding.transportPrefGroup.getCheckedRadioButtonId());
            if(transportPrefBtn.getText().toString().equalsIgnoreCase(noTransportPref.stringLabel))
                registerObject.setPrefGender(noTransportPref.intValue);
            else if(transportPrefBtn.getText().toString().equalsIgnoreCase(walk.stringLabel))
                registerObject.setPrefGender(walk.intValue);
            else if(transportPrefBtn.getText().toString().equalsIgnoreCase(taxi.stringLabel))
                registerObject.setPrefGender(taxi.intValue);



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
                        Log.e("TAG", "onFailure: "+registerResponse.getState().toString());
                        if(registerResponse.getData().getMessage().equals("Incorrect data: Either Email exists or Username exists or Password did not match"))
                        {
                            Toast.makeText(this, "Registration Failed because of email match or username match or password", Toast.LENGTH_SHORT).show();
                            Intent myIntent2 = new Intent(RegisterActivity.this, RegisterActivity.class);
                            startActivity(myIntent2);
                            finish();
                            break;
                        }
                        else {
                            layoutDataBinding.progressBarOverlay.setVisibility(View.GONE);
                            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                            break;
                        }
                }
            });

        }
    }
}
