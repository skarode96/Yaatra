package com.tcd.yaatra.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.tcd.yaatra.R;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DailyCommuteActivity extends AppCompatActivity {

    private static final String TAG = "Response";
    Button getReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_commute);
        getWindow().setBackgroundDrawableResource(R.drawable.journey_sharing_login_background);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String tokenValue = extras.getString("token");
            final TextView textViewToChange = (TextView) findViewById(R.id.LoginToken);
            textViewToChange.setText(tokenValue);

            final RequestQueue requestQueue = Volley.newRequestQueue(this);
            final String url = "https://yaatra-services.herokuapp.com/dailycommute/v1/";
            getReq = (Button) findViewById(R.id.btnDailyCommute);

            final ArrayList<HashMap<String, String>> commuteList = new ArrayList<>();
            getReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    JsonArrayRequest req = new JsonArrayRequest(Request.Method.GET, url,
                            null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONArray json = null; /*
                                HashMap<String, String> mylist = new HashMap<String, String>();
                                json = new JSONArray(response);
                                for (int i = 0; i < json.length(); i++) {
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    JsonArray e = json.getJSONArray(i);
                                    String user = e.getString("User");
                                    String source = e.getString("Source");
                                    String destination = e.getString("Destination");
                                    map.put("User", user);
                                    map.put("Source", source);
                                    map.put("Destination", destination);
                                    commuteList.add(map);*/
                            Log.e("Rest Response", response.toString());
                            /*Toast.makeText(DailyCommuteActivity.this, "" + response.toString(), Toast.LENGTH_SHORT).show();*/
                            Intent myIntent = new Intent(DailyCommuteActivity.this, UserActivity.class);
                            myIntent.putExtra("user", response.toString());
                            DailyCommuteActivity.this.startActivity(myIntent);
                        }

                    },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    VolleyLog.d("Error", "Error: " + error.getMessage());
                                    Toast.makeText(DailyCommuteActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", "Token " + tokenValue);
                            return headers;
                        }
                    };
                    requestQueue.add(req);
                }
            });

            /*For Show Routes*/
            Button btnRoute;
            btnRoute = (Button) findViewById(R.id.btnMapBox);
            btnRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(DailyCommuteActivity.this, DirectActivity.class);
                    startActivity(myIntent);
                }
            });

            /*For Find Co Travellers*/
            Button btnFind;
            btnFind = (Button) findViewById(R.id.btnFind);
            btnFind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(DailyCommuteActivity.this, FindActivity.class);
                    startActivity(myIntent);
                }
            });
        }
    }

}
