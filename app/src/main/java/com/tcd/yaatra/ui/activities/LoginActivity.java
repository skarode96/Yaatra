package com.tcd.yaatra.ui.activities;

import android.util.Log;

import com.tcd.yaatra.R;
import com.tcd.yaatra.databinding.ActivityLoginBinding;
import com.tcd.yaatra.services.api.yaatra.api.LoginApi;
import com.tcd.yaatra.services.api.yaatra.models.LoginResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    @Inject
    LoginApi loginApi;


    @Override
    int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    public void initEventHandlers() {
        super.initEventHandlers();

        layoutDataBinding.login.setOnClickListener(view -> {
            Call<LoginResponse> loginRequest = loginApi.getToken("test", "qwerty12340");
            loginRequest.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    Log.e("SUNIT", "SUCCESS "+ response.body().toString());
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("SUNIT", "FAILED");
                }
            });
        });
    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_login);
//        getWindow().setBackgroundDrawableResource(R.drawable.journey_sharing_login_background);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        final RequestQueue requestQueue = Volley.newRequestQueue(this);
//        final String URL = "https://yaatra-services.herokuapp.com/login/v1/";
//        postReq = (Button) findViewById(R.id.login);
//        postReq.setOnClickListener(new View.OnClickListener() {
//
//            AutoCompleteTextView username = findViewById(R.id.username);
//            AutoCompleteTextView password = findViewById(R.id.password);
//
//            @Override
//            public void onClick(View v) {
//
//                final String usernameEntered = username.getText().toString();
//                Log.d("Tag: ", usernameEntered);
//                final String passwordEntered = password.getText().toString();
//                Log.d("Tag: ", passwordEntered);
//                if (!usernameEntered.equals("test") || !passwordEntered.equals("qwerty12340")) {
//                    Toast.makeText(LoginActivity.this, "" + "User name or password doesn't exists. Please Sign-Up", Toast.LENGTH_SHORT).show();
//                    Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
//                    LoginActivity.this.startActivity(myIntent);
//                } else {
//                    StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
//                            URL,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//
//                                    System.out.println("response" + response);
//                                    try {
//                                        JSONObject object = new JSONObject(response);
//                                        String token = (String) object.get("token");
//
//                                        Intent myIntent = new Intent(LoginActivity.this, DailyCommuteActivity.class);
//                                        myIntent.putExtra("token", token);
//                                        startActivity(myIntent);
//
//
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }, new Response.ErrorListener() {
//
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                        }
//                    }) {
//
//                        @Override
//                        public String getBodyContentType() {
//                            return "application/x-www-form-urlencoded; charset=UTF-8";
//                        }
//
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            Map<String, String> postParam = new HashMap<String, String>();
//
//                            postParam.put("username", usernameEntered);
//                            postParam.put("password", passwordEntered);
//                            return postParam;
//                        }
//
//                    };
//                    requestQueue.add(jsonObjRequest);
//                }
//
//            }
//        });
//
//    }
}
