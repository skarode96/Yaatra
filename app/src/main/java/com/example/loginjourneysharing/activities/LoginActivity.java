package com.example.loginjourneysharing.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.loginjourneysharing.R;

import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "Response";
    Button postReq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setBackgroundDrawableResource(R.drawable.journey_sharing_login_background);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String URL = "https://yaatra-services.herokuapp.com/login/v1/";
        postReq = (Button) findViewById(R.id.login);
        postReq.setOnClickListener(new View.OnClickListener() {

            AutoCompleteTextView username = findViewById(R.id.username);
            AutoCompleteTextView password = findViewById(R.id.password);

            @Override
            public void onClick(View v) {

                final String usernameEntered = "test";
                Log.d("Tag: ", usernameEntered);
                final String passwordEntered = "qwerty12340";
                Log.d("Tag: ", passwordEntered);
                if (!usernameEntered.equals("test") || !passwordEntered.equals("qwerty12340")) {
                    Toast.makeText(LoginActivity.this, "" + "User name or password doesn't exists. Please Sign-Up", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                } else {
                    StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                            URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    System.out.println("response" + response);
                                    try {
                                        JSONObject object = new JSONObject(response);
                                        String token = (String) object.get("token");

                                        Intent myIntent = new Intent(LoginActivity.this, DailyCommuteActivity.class);
                                        myIntent.putExtra("token", token);
                                        startActivity(myIntent);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {

                        @Override
                        public String getBodyContentType() {
                            return "application/x-www-form-urlencoded; charset=UTF-8";
                        }

                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> postParam = new HashMap<String, String>();

                            postParam.put("username", usernameEntered);
                            postParam.put("password", passwordEntered);
                            return postParam;
                        }

                    };
                    requestQueue.add(jsonObjRequest);
                }

            }
        });

    }
}
