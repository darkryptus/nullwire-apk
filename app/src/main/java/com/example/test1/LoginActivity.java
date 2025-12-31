package com.example.test1;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;

import android.content.SharedPreferences;
import android.content.Context;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.androidnetworking.interfaces.StringRequestListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Fast Android Networking
        AndroidNetworking.initialize(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        EditText emailXml = findViewById(R.id.email);
        EditText passwdXml = findViewById(R.id.passwd);
        Button submitBtn = findViewById(R.id.submitBtn);


        submitBtn.setOnClickListener(v -> {
        
        String email = emailXml.getText().toString();
        String passwd = passwdXml.getText().toString();

            
            if (email.isEmpty() || passwd.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
               // Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                sendPostRequest(email, passwd, new PostRequestCallback() {
                    @Override
                    public void onSuccess(String code) {
                        if (code.equals("emailNotExist")) {
                        Toast.makeText(LoginActivity.this, "The account does not exist", Toast.LENGTH_SHORT).show();
                        
                        } else if (code.equals("invalidPsswd")) {
                        Toast.makeText(LoginActivity.this, "The Password is incorrect", Toast.LENGTH_SHORT).show();
                        
                        } else if (code.equals("success")) {
                            
                            editor.putString("email", email);
                            editor.putString("passwd", passwd);
                            
                            editor.apply();
                            
                            Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                            
                            Intent ChatActivity = new Intent(LoginActivity.this, ChatActivity.class);
                            ChatActivity.putExtra("email", email);
                            startActivity(ChatActivity);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
     
                            
                        }
                    }

                    @Override
                    public void onFailure(int code) {
                        Toast.makeText(LoginActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        
                    }
                });

            }
        });
    }

    // sendPostRequest
    public void sendPostRequest(String email, String passwd, PostRequestCallback callback) {
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put("email", email);
            jsonObjectToSend.put("passwd", passwd);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("http://192.0.0.4:7272/validator")
            .addJSONObjectBody(jsonObjectToSend)
            .build()
            .getAsString( new StringRequestListener() {
                @Override
                public void onResponse(String response) {
                    
                        if (!response.equals("")) {
                        runOnUiThread(() -> callback.onSuccess(response)); // UI thread for Toast
                }

                    // runOnUiThread(() -> callback.onSuccess(69)); // UI thread for Toast
                }

                @Override
                public void onError(ANError anError) {
                    Log.d("server response", anError.toString());
                    runOnUiThread(() -> callback.onFailure(404)); // UI thread for Toast
                }
            });
    }

    // Callback interface for handling success and failure
    interface PostRequestCallback {
        void onSuccess(String code);
        void onFailure(int code);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
