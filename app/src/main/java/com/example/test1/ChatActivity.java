package com.example.test1;

import android.content.SharedPreferences;
import android.content.Context;
import android.os.Message;
import android.text.Layout;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.widget.LinearLayout;
import android.view.Gravity;

public class ChatActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize Fast Android Networking
        AndroidNetworking.initialize(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
        
        LinearLayout settingsDiv = findViewById(R.id.settings);
        
        settingsDiv.setOnClickListener(v -> {
            Intent SettingsAcitivity = new Intent(ChatActivity.this, SettingsActivity.class);
            startActivity(SettingsAcitivity);
        });
                
        LinearLayout rootLayout = findViewById(R.id.rootLayout);
        
        String email;
        
        email = getIntent().getStringExtra("email");
        
        if (email == null) {
            SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            
             email = prefs.getString("email", "null"); //2nd argument is for default value if not found
       
        } else {
             email = getIntent().getStringExtra("email");
        }
        
        sendPostRequest(email, new PostRequestCallback() {
                    @Override
                    public void onSuccess(JSONObject resp) {
            
                        String obj1 = "";
                        JSONArray obj2 = new JSONArray();
                        try {
                            obj1 = resp.getString("usr");
                            obj2 = resp.getJSONArray("usrs420"); // CONVERTING JSONOBJECT TO JSOnARRAY
                            
                        } catch (JSONException e) {
                            Toast.makeText(ChatActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            
                        }
                
                        int i = 1;
                
                        while (i < obj2.length()) {
                
                           LinearLayout div = new LinearLayout(ChatActivity.this);
                           TextView text = new TextView(ChatActivity.this);
                    
                            div.setBackgroundColor(getResources().getColor(android.R.color.black));
                            
                            div.setLayoutParams( new LinearLayout.LayoutParams (
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                100
                    ));
                    
                 // defined this var to avoid lambda error below (div onclick listener)
                 
                    String obj2Copy = "";
                 
                    try {
                    obj2Copy = obj2.getString(i);
                    }  catch (JSONException e) {
                             Toast.makeText(ChatActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        
                    final String finalObj2Copy = obj2Copy;
                    final String usr0 = obj1;
                    
                    div.setOnClickListener(v -> {
                        
                        Intent MessageActivity = new Intent(ChatActivity.this, MessageActivity.class);
                        
                        MessageActivity.putExtra("usr", usr0.toLowerCase());
                        MessageActivity.putExtra("usrs", finalObj2Copy.toLowerCase());
      
                        startActivity(MessageActivity);
                        
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
      
                    });
                    
                    
                        div.setGravity(Gravity.CENTER_VERTICAL);
                           
                           try {
                           text.setText(capitalizeFirstLetter(obj2.getString(i)));
                            
                        }    catch (JSONException e) {
                             Toast.makeText(ChatActivity.this, "JSON Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    
                      LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                    
                        params1.leftMargin = 20;
                    
                        text.setLayoutParams(params1);
                            
                            div.addView(text);
                            rootLayout.addView(div);
                            
                        i++;
                    }
    
                        Toast.makeText(ChatActivity.this, obj1 + " , " + obj2.length(), Toast.LENGTH_SHORT).show();
                        
                        
                    }

                    @Override
                    public void onFailure(int code) {
                        Toast.makeText(ChatActivity.this, "Error connecting to server", Toast.LENGTH_SHORT).show();
                
                
                        
                
                        
                    }
        
            });

}
    
    public static String capitalizeFirstLetter(String str) {
    if (str == null || str.isEmpty()) {
        return str; // Return as is if null or empty
    }
    return str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
}

    
                // sendPostRequest
    public void sendPostRequest(String email, PostRequestCallback callback) {
        JSONObject jsonObjectToSend = new JSONObject();
        try {
            jsonObjectToSend.put("email", email);
            
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post("https://nullwire.us.to/dataRetreiver")
            .addJSONObjectBody(jsonObjectToSend)
            .build()
            .getAsJSONObject( new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    
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
        void onSuccess(JSONObject resp);
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
