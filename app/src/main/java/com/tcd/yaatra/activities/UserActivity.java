package com.tcd.yaatra.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.tcd.yaatra.R;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        /*getWindow().setBackgroundDrawableResource(R.drawable.journey_sharing_login_background);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);*/

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String user = extras.getString("user");
            final TextView textViewToChange = (TextView) findViewById(R.id.userView);
            textViewToChange.setText(user);
        }
    }
}
