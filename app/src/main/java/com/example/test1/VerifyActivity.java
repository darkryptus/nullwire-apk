package com.example.test1;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import com.androidnetworking.interfaces.StringRequestListener;
import org.json.JSONException;
import org.json.JSONObject;

public class VerifyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);


        // HEY WE GOT TWO THINGS IF I WANNA
        // GET A VARS CURRENT VALUE WITHOUT
        // EITHER RUNNING IN LOOP OR CALL THAT
        // WHEN YOU NEED IT
        // SO MAKE A NEW VAR WHENEVER YOU NEED
        // THAT VAR'S CURRENT VALUE
        // NOT TESTED SO UNSURE

        // Initialize Fast Android Networking
        AndroidNetworking.initialize(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
        
        String email = getIntent().getStringExtra("email");
        String passwd = getIntent().getStringExtra("passwd");
        String usr = getIntent().getStringExtra("usr");
        
        TextView text1 = findViewById(R.id.text1);
        text1.setText("Enter the OTP sent to " + email);
        
        sendPostRequest(email);
        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
        
        
        //new bakchodi
        Button SubmitBtn = findViewById(R.id.submitBtn);
        
        SubmitBtn.setOnClickListener(v -> {
        
        EditText otpXml = findViewById(R.id.otp);
        
        String otpStr = otpXml.getText().toString();
        
        if (otpStr.isEmpty()) {
        Toast.makeText(VerifyActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
        return; // Stop execution if OTP is empty
    }
        
        int otp = Integer.parseInt(otpStr);
        
        sendPostRequest(otp, new PostRequestCallback() {
            @Override
                    public void onSuccess(String code) {
                        if (code.equals("invalid")) {
                            Toast.makeText(VerifyActivity.this, "The otp is invalid", Toast.LENGTH_SHORT).show();
                            
                        } else if (code.equals("valid")) {
                            Toast.makeText(VerifyActivity.this, "valid", Toast.LENGTH_SHORT).show();
                            
                            // now here make the account of the user
                            // via add endpoint
                        
                        sendPostRequestAdd(email, passwd, usr, new PostRequestCallback() {
                    @Override
                    public void onSuccess(String code) {
                        if (code.equals("ok")) {
                            Toast.makeText(VerifyActivity.this, "Account created!", Toast.LENGTH_SHORT).show();
                        }
                        
                    }

                    @Override
                    public void onFailure(int code) {
                        if (code == 404) {
                        Toast.makeText(VerifyActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        
                        } else if (code == 500) {
                            Toast.makeText(VerifyActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                            }
                    }
        });
                        
                            //end 
                            
                        }
                        
                    }

                    @Override
                    public void onFailure(int code) {
                        Toast.makeText(VerifyActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                        
                    }
        });
            
    });
        
    }
        
        // sendPostRequest MAIL
    public void sendPostRequest(String email) {
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put("emailSend", email);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("http://192.0.0.4:7272/mail")
            .addJSONObjectBody(jsonObjectToSend)
            .build()
            .getAsString( new StringRequestListener() {
                @Override
                public void onResponse(String response) {
                    
                        if (!response.equals("")) {
                        runOnUiThread(() -> Log.d("mail post", "true")); // UI thread for Toast
                }

                    // runOnUiThread(() -> callback.onSuccess(69)); // UI thread for Toast
                }

                @Override
                public void onError(ANError anError) {
                    Log.d("server response mail", anError.toString());
                    runOnUiThread(() -> Log.d("mail post", "false")); // UI thread for Toast
                }
            });
    }
   
    
    
        // sendPostRequest MAIL OTP
    public void sendPostRequest(int otp, PostRequestCallback callback) {
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put("codeInput", otp);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("http://192.0.0.4:7272/otp")
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

    
      // sendPostRequest ADD
    public void sendPostRequestAdd(String email, String passwd, String usr, PostRequestCallback callback) {
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put("email", email);
            jsonObjectToSend.put("passwd", passwd);
            jsonObjectToSend.put("usr", usr);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("http://192.0.0.4:7272/add")
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

