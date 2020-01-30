package com.tcd.yaatra.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.tcd.yaatra.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MapBoxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapbox_input);
        getWindow().setBackgroundDrawableResource(R.drawable.journey_sharing_login_background);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public void showRoutes(View v) throws UnsupportedEncodingException {

        EditText dst = findViewById(R.id.editText);
        String dst_parsed = URLEncoder.encode(dst.getText().toString(), "utf-8");
        Intent mapIntent = new Intent(MapBoxActivity.this, MapActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("Destination",dst_parsed);
        mapIntent.putExtras(bundle);
        startActivity(mapIntent);

    }
}
