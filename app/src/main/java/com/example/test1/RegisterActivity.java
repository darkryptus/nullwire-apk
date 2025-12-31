package com.example.test1;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;

import com.androidnetworking.interfaces.StringRequestListener;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    
    public String email;
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Fast Android Networking
        AndroidNetworking.initialize(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
        
        
        EditText usrXml = findViewById(R.id.usr);
        EditText emailXml = findViewById(R.id.email);
        EditText passwdXml = findViewById(R.id.passwd);
        Button submitBtn = findViewById(R.id.submitBtn);


        submitBtn.setOnClickListener(v -> {
        
        String usr = usrXml.getText().toString();
        email = emailXml.getText().toString();
        String passwd = passwdXml.getText().toString();
            
            if (email.isEmpty() || passwd.isEmpty() || usr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
               // Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();

                sendPostRequest(usr, email, new PostRequestCallback() {
                    @Override
                    public void onSuccess(String code) {
                        if (code.equals("emailAlreadyExist")) {
                        Toast.makeText(RegisterActivity.this, "An account with this email already exists", Toast.LENGTH_SHORT).show();
                        
                        } else if (code.equals("usrAlreadyExist")) {
                        Toast.makeText(RegisterActivity.this, "This username is taken", Toast.LENGTH_SHORT).show();
                        
                        } else if (code.equals("ok")) {
                            Toast.makeText(RegisterActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            
                            Intent VerifyActivity = new Intent(RegisterActivity.this, VerifyActivity.class);
                            VerifyActivity.putExtra("email", email);
                            VerifyActivity.putExtra("passwd", passwd);
                            VerifyActivity.putExtra("usr", usr);
                            startActivity(VerifyActivity);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
     
                            
                        }
                    }

                    @Override
                    public void onFailure(int code) {
                        Toast.makeText(RegisterActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        
                    }
                });

            }
        });
    }

    // sendPostRequest
    public void sendPostRequest(String usr, String email, PostRequestCallback callback) {
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put("usr", usr);
            jsonObjectToSend.put("email", email);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("http://192.0.0.4:7272/checker")
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
