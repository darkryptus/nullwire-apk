package com.example.test1;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.os.Build;

import android.view.View;
import android.content.SharedPreferences;
import android.content.Context;

import android.widget.LinearLayout;

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

public class SettingsActivity extends AppCompatActivity {
    
private boolean clicked;
        
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Fast Android Networking
        AndroidNetworking.initialize(getApplicationContext());

        SharedPreferences prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }
        
        Button logout = findViewById(R.id.logout);
        Button theme = findViewById(R.id.theme);
        LinearLayout themeBelow = findViewById(R.id.themeBelow);
        
        logout.setOnClickListener(v -> {
            
            editor.remove("email");
            editor.remove("passwd");
            
            if (editor.commit()) {
            
            Intent MainActivity = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(MainActivity);
            }
            
            }
        );
        
        
        
        clicked = false;

        theme.setOnClickListener(v -> {
        if (!clicked) {
            
                clicked = true;
            
                Toast.makeText(SettingsActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            
                themeBelow.setVisibility(View.VISIBLE);
            
            } else {
                    
                clicked = false;
            
                Toast.makeText(SettingsActivity.this, "clicked 2", Toast.LENGTH_SHORT).show();
                themeBelow.setVisibility(View.GONE);
          
                }
            
        });
        
        

    }



}
